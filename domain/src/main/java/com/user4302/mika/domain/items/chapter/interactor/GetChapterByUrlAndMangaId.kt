package com.user4302.mika.domain.items.chapter.interactor

import com.user4302.mika.domain.items.chapter.model.Chapter
import com.user4302.mika.domain.items.chapter.repository.ChapterRepository

class GetChapterByUrlAndMangaId(
    private val chapterRepository: ChapterRepository,
) {

    suspend fun await(url: String, sourceId: Long): Chapter? {
        return try {
            chapterRepository.getChapterByUrlAndMangaId(url, sourceId)
        } catch (e: Exception) {
            null
        }
    }
}
