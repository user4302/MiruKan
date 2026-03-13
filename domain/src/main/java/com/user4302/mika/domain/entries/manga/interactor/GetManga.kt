package com.user4302.mika.domain.entries.manga.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.domain.entries.manga.repository.MangaRepository
import kotlinx.coroutines.flow.Flow
import logcat.LogPriority

class GetManga(
    private val mangaRepository: MangaRepository,
) {

    suspend fun await(id: Long): Manga? {
        return try {
            mangaRepository.getMangaById(id)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            null
        }
    }

    suspend fun subscribe(id: Long): Flow<Manga> {
        return mangaRepository.getMangaByIdAsFlow(id)
    }

    fun subscribe(url: String, sourceId: Long): Flow<Manga?> {
        return mangaRepository.getMangaByUrlAndSourceIdAsFlow(url, sourceId)
    }
}
