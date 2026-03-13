package com.user4302.mika.util.chapter

import com.user4302.mika.data.download.manga.MangaDownloadCache
import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.domain.items.chapter.model.Chapter
import com.user4302.mika.source.local.entries.manga.isLocal
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * Returns a copy of the list with not downloaded chapters removed.
 */
fun List<Chapter>.filterDownloadedChapters(manga: Manga): List<Chapter> {
    if (manga.isLocal()) return this

    val downloadCache: MangaDownloadCache = Injekt.get()

    return filter {
        downloadCache.isChapterDownloaded(
            it.name,
            it.scanlator,
            manga.title,
            manga.source,
            false,
        )
    }
}
