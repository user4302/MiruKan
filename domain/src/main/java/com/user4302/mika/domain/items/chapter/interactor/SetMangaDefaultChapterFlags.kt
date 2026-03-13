package com.user4302.mika.domain.items.chapter.interactor

import com.user4302.mika.core.common.util.lang.withNonCancellableContext
import com.user4302.mika.domain.entries.manga.interactor.GetMangaFavorites
import com.user4302.mika.domain.entries.manga.interactor.SetMangaChapterFlags
import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.domain.library.service.LibraryPreferences

class SetMangaDefaultChapterFlags(
    private val libraryPreferences: LibraryPreferences,
    private val setMangaChapterFlags: SetMangaChapterFlags,
    private val getFavorites: GetMangaFavorites,
) {

    suspend fun await(manga: Manga) {
        withNonCancellableContext {
            with(libraryPreferences) {
                setMangaChapterFlags.awaitSetAllFlags(
                    mangaId = manga.id,
                    unreadFilter = filterChapterByRead().get(),
                    downloadedFilter = filterChapterByDownloaded().get(),
                    bookmarkedFilter = filterChapterByBookmarked().get(),
                    sortingMode = sortChapterBySourceOrNumber().get(),
                    sortingDirection = sortChapterByAscendingOrDescending().get(),
                    displayMode = displayChapterByNameOrNumber().get(),
                )
            }
        }
    }

    suspend fun awaitAll() {
        withNonCancellableContext {
            getFavorites.await().forEach { await(it) }
        }
    }
}
