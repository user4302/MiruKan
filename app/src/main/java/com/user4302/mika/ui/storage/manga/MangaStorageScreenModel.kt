package com.user4302.mika.ui.storage.manga

import cafe.adriel.voyager.core.model.screenModelScope
import com.user4302.mika.core.common.util.lang.launchNonCancellable
import com.user4302.mika.data.download.manga.MangaDownloadCache
import com.user4302.mika.data.download.manga.MangaDownloadManager
import com.user4302.mika.domain.category.manga.interactor.GetMangaCategories
import com.user4302.mika.domain.category.manga.interactor.GetVisibleMangaCategories
import com.user4302.mika.domain.entries.manga.interactor.GetLibraryManga
import com.user4302.mika.domain.library.manga.LibraryManga
import com.user4302.mika.domain.source.manga.service.MangaSourceManager
import com.user4302.mika.ui.storage.CommonStorageScreenModel
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class MangaStorageScreenModel(
    downloadCache: MangaDownloadCache = Injekt.get(),
    private val getLibraries: GetLibraryManga = Injekt.get(),
    getCategories: GetMangaCategories = Injekt.get(),
    getVisibleCategories: GetVisibleMangaCategories = Injekt.get(),
    private val downloadManager: MangaDownloadManager = Injekt.get(),
    private val sourceManager: MangaSourceManager = Injekt.get(),
) : CommonStorageScreenModel<LibraryManga>(
    downloadCacheChanges = downloadCache.changes,
    downloadCacheIsInitializing = downloadCache.isInitializing,
    libraries = getLibraries.subscribe(),
    categories = { hideHiddenCategories ->
        if (hideHiddenCategories) {
            getVisibleCategories.subscribe()
        } else {
            getCategories.subscribe()
        }
    },
    getDownloadSize = { downloadManager.getDownloadSize(manga) },
    getDownloadCount = { downloadManager.getDownloadCount(manga) },
    getId = { id },
    getCategoryId = { category },
    getTitle = { manga.title },
    getThumbnail = { manga.thumbnailUrl },
) {
    override fun deleteEntry(id: Long) {
        screenModelScope.launchNonCancellable {
            val manga = getLibraries.await().find {
                it.id == id
            }?.manga ?: return@launchNonCancellable
            val source = sourceManager.get(manga.source) ?: return@launchNonCancellable
            downloadManager.deleteManga(manga, source)
        }
    }
}
