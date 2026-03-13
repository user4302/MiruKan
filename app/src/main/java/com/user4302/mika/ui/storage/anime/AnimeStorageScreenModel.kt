package com.user4302.mika.ui.storage.anime

import cafe.adriel.voyager.core.model.screenModelScope
import com.user4302.mika.core.common.util.lang.launchNonCancellable
import com.user4302.mika.data.download.anime.AnimeDownloadCache
import com.user4302.mika.data.download.anime.AnimeDownloadManager
import com.user4302.mika.domain.category.anime.interactor.GetAnimeCategories
import com.user4302.mika.domain.category.anime.interactor.GetVisibleAnimeCategories
import com.user4302.mika.domain.entries.anime.interactor.GetLibraryAnime
import com.user4302.mika.domain.library.anime.LibraryAnime
import com.user4302.mika.domain.source.anime.service.AnimeSourceManager
import com.user4302.mika.ui.storage.CommonStorageScreenModel
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class AnimeStorageScreenModel(
    downloadCache: AnimeDownloadCache = Injekt.get(),
    private val getLibraries: GetLibraryAnime = Injekt.get(),
    getCategories: GetAnimeCategories = Injekt.get(),
    getVisibleCategories: GetVisibleAnimeCategories = Injekt.get(),
    private val downloadManager: AnimeDownloadManager = Injekt.get(),
    private val sourceManager: AnimeSourceManager = Injekt.get(),
) : CommonStorageScreenModel<LibraryAnime>(
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
    getDownloadSize = { downloadManager.getDownloadSize(anime) },
    getDownloadCount = { downloadManager.getDownloadCount(anime) },
    getId = { id },
    getCategoryId = { category },
    getTitle = { anime.title },
    getThumbnail = { anime.thumbnailUrl },
) {
    override fun deleteEntry(id: Long) {
        screenModelScope.launchNonCancellable {
            val anime = getLibraries.await().find {
                it.id == id
            }?.anime ?: return@launchNonCancellable
            val source = sourceManager.get(anime.source) ?: return@launchNonCancellable
            downloadManager.deleteAnime(anime, source)
        }
    }
}
