package com.user4302.mika.util.chapter

import com.user4302.domain.items.chapter.model.applyFilters
import com.user4302.mika.data.download.manga.MangaDownloadManager
import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.domain.items.chapter.model.Chapter
import com.user4302.mika.ui.entries.manga.ChapterList

/**
 * Gets next unread chapter with filters and sorting applied
 */
fun List<Chapter>.getNextUnread(manga: Manga, downloadManager: MangaDownloadManager): Chapter? {
    return applyFilters(manga, downloadManager).let { chapters ->
        if (manga.sortDescending()) {
            chapters.findLast { !it.read }
        } else {
            chapters.find { !it.read }
        }
    }
}

/**
 * Gets next unread chapter with filters and sorting applied
 */
fun List<ChapterList.Item>.getNextUnread(manga: Manga): Chapter? {
    return applyFilters(manga).let { chapters ->
        if (manga.sortDescending()) {
            chapters.findLast { !it.chapter.read }
        } else {
            chapters.find { !it.chapter.read }
        }
    }?.chapter
}
