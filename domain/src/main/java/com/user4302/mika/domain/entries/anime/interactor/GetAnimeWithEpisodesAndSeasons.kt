package com.user4302.mika.domain.entries.anime.interactor

import com.user4302.mika.domain.anime.SeasonAnime
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.domain.entries.anime.repository.AnimeRepository
import com.user4302.mika.domain.items.episode.model.Episode
import com.user4302.mika.domain.items.episode.repository.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetAnimeWithEpisodesAndSeasons(
    private val animeRepository: AnimeRepository,
    private val episodeRepository: EpisodeRepository,
) {

    suspend fun subscribe(id: Long): Flow<Triple<Anime, List<Episode>, List<SeasonAnime>>> {
        return combine(
            animeRepository.getAnimeByIdAsFlow(id),
            episodeRepository.getEpisodeByAnimeIdAsFlow(id),
            animeRepository.getAnimeSeasonsByIdAsFlow(id),
        ) { anime, episodes, seasons ->
            Triple(anime, episodes, seasons)
        }
    }

    suspend fun awaitAnime(id: Long): Anime {
        return animeRepository.getAnimeById(id)
    }

    suspend fun awaitEpisodes(id: Long): List<Episode> {
        return episodeRepository.getEpisodeByAnimeId(id)
    }

    suspend fun awaitSeasons(id: Long): List<SeasonAnime> {
        return animeRepository.getAnimeSeasonsById(id)
    }
}
