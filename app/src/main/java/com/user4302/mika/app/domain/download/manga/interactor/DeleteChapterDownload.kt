package com.user4302.domain.download.manga.interactor

import com.user4302.mika.core.common.util.lang.withNonCancellableContext
import com.user4302.mika.data.download.manga.MangaDownloadManager
import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.domain.items.chapter.model.Chapter
import com.user4302.mika.domain.source.manga.service.MangaSourceManager

class DeleteChapterDownload(
    private val sourceManager: MangaSourceManager,
    private val downloadManager: MangaDownloadManager,
) {

    suspend fun awaitAll(manga: Manga, vararg chapters: Chapter) = withNonCancellableContext {
        sourceManager.get(manga.source)?.let { source ->
            downloadManager.deleteChapters(chapters.toList(), manga, source)
        }
    }
}
