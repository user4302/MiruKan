package com.user4302.mika.domain.items.chapter.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.items.chapter.model.ChapterUpdate
import com.user4302.mika.domain.items.chapter.repository.ChapterRepository
import logcat.LogPriority

class UpdateChapter(
    private val chapterRepository: ChapterRepository,
) {

    suspend fun await(chapterUpdate: ChapterUpdate) {
        try {
            chapterRepository.updateChapter(chapterUpdate)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
        }
    }

    suspend fun awaitAll(chapterUpdates: List<ChapterUpdate>) {
        try {
            chapterRepository.updateAllChapters(chapterUpdates)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
        }
    }
}
