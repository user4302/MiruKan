package com.user4302.domain.entries.anime.interactor

import com.user4302.mika.core.common.util.lang.toLong
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.domain.entries.anime.model.AnimeUpdate
import com.user4302.mika.domain.entries.anime.repository.AnimeRepository
import kotlin.math.pow

class SetAnimeViewerFlags(
    private val animeRepository: AnimeRepository,
) {

    suspend fun awaitSetSkipIntroLength(id: Long, flag: Long) {
        val anime = animeRepository.getAnimeById(id)
        animeRepository.updateAnime(
            AnimeUpdate(
                id = id,
                viewerFlags = anime.viewerFlags
                    .setFlag(flag, Anime.ANIME_INTRO_MASK)
                    // Disable skip intro button if length is set to 0
                    .setFlag((flag == 0L).toLong().addHexZeros(14), Anime.ANIME_INTRO_DISABLE_MASK),
            ),
        )
    }

    suspend fun awaitSetNextEpisodeAiring(id: Long, flags: Pair<Int, Long>) {
        awaitSetNextEpisodeToAir(id, flags.first.toLong().addHexZeros(zeros = 2))
        awaitSetNextEpisodeAiringAt(id, flags.second.addHexZeros(zeros = 6))
    }

    private suspend fun awaitSetNextEpisodeToAir(id: Long, flag: Long) {
        val anime = animeRepository.getAnimeById(id)
        animeRepository.updateAnime(
            AnimeUpdate(
                id = id,
                viewerFlags = anime.viewerFlags.setFlag(flag, Anime.ANIME_AIRING_EPISODE_MASK),
            ),
        )
    }

    private suspend fun awaitSetNextEpisodeAiringAt(id: Long, flag: Long) {
        val anime = animeRepository.getAnimeById(id)
        animeRepository.updateAnime(
            AnimeUpdate(
                id = id,
                viewerFlags = anime.viewerFlags.setFlag(flag, Anime.ANIME_AIRING_TIME_MASK),
            ),
        )
    }

    private fun Long.setFlag(flag: Long, mask: Long): Long {
        return this and mask.inv() or (flag and mask)
    }

    private fun Long.addHexZeros(zeros: Int): Long {
        val hex = 16.0
        return this.times(hex.pow(zeros)).toLong()
    }
}
