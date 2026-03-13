package com.user4302.mika.domain.category.manga.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.entries.manga.repository.MangaRepository
import logcat.LogPriority

class SetMangaCategories(
    private val mangaRepository: MangaRepository,
) {

    suspend fun await(mangaId: Long, categoryIds: List<Long>) {
        try {
            mangaRepository.setMangaCategories(mangaId, categoryIds)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
        }
    }
}
