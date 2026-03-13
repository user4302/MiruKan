package com.user4302.mika.domain.items.chapter.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.items.chapter.model.Chapter
import com.user4302.mika.domain.items.chapter.repository.ChapterRepository
import logcat.LogPriority

class GetChaptersByMangaId(
    private val chapterRepository: ChapterRepository,
) {

    suspend fun await(mangaId: Long, applyScanlatorFilter: Boolean = false): List<Chapter> {
        return try {
            chapterRepository.getChapterByMangaId(mangaId, applyScanlatorFilter)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            emptyList()
        }
    }
}
