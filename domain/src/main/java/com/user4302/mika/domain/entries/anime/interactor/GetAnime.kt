package com.user4302.mika.domain.entries.anime.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.domain.entries.anime.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow
import logcat.LogPriority

class GetAnime(
    private val animeRepository: AnimeRepository,
) {

    suspend fun await(id: Long): Anime? {
        return try {
            animeRepository.getAnimeById(id)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            null
        }
    }

    suspend fun subscribe(id: Long): Flow<Anime> {
        return animeRepository.getAnimeByIdAsFlow(id)
    }

    fun subscribe(url: String, sourceId: Long): Flow<Anime?> {
        return animeRepository.getAnimeByUrlAndSourceIdAsFlow(url, sourceId)
    }
}
