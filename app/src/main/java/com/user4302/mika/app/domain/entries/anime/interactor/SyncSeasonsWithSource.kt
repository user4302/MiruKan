package com.user4302.domain.entries.anime.interactor

import com.user4302.domain.entries.anime.model.toDomainAnime
import com.user4302.mika.animesource.AnimeSource
import com.user4302.mika.animesource.model.SAnime
import com.user4302.mika.domain.entries.anime.interactor.NetworkToLocalAnime
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.domain.entries.anime.model.NoSeasonsException
import com.user4302.mika.domain.entries.anime.model.toAnimeUpdate
import com.user4302.mika.domain.entries.anime.repository.AnimeRepository
import com.user4302.mika.domain.items.season.interactor.GetAnimeSeasonsByParentId
import com.user4302.mika.domain.items.season.interactor.ShouldUpdateDbSeason
import com.user4302.mika.domain.items.season.service.SeasonRecognition
import com.user4302.mika.source.local.entries.anime.isLocal
import java.time.ZonedDateTime

class SyncSeasonsWithSource(
    private val updateAnime: UpdateAnime,
    private val animeRepository: AnimeRepository,
    private val networkToLocalAnime: NetworkToLocalAnime,
    private val shouldUpdateDbSeason: ShouldUpdateDbSeason,
    private val getAnimeSeasonsByParentId: GetAnimeSeasonsByParentId,
) {
    suspend fun await(
        rawSourceSeasons: List<SAnime>,
        anime: Anime,
        source: AnimeSource,
        manualFetch: Boolean = false,
        fetchWindow: Pair<Long, Long> = Pair(0, 0),
    ): List<Anime> {
        if (rawSourceSeasons.isEmpty() && !source.isLocal()) {
            throw NoSeasonsException()
        }

        val now = ZonedDateTime.now()

        val sourceSeasons = rawSourceSeasons
            .distinctBy { it.url }
            .mapIndexed { i, sAnime ->
                networkToLocalAnime.await(sAnime.toDomainAnime(source.id))
                    .copy(parentId = anime.id, seasonSourceOrder = i.toLong())
            }

        val dbSeasons = getAnimeSeasonsByParentId.await(anime.id)

        val newSeasons = mutableListOf<Anime>()
        val updatedSeasons = mutableListOf<Anime>()
        val removedSeasons = dbSeasons.filterNot { dbSeasons ->
            sourceSeasons.any { sourceSeason ->
                dbSeasons.anime.url == sourceSeason.url
            }
        }

        for (sourceSeason in sourceSeasons) {
            var season = sourceSeason

            // Recognize season number for the season
            val seasonNumber = SeasonRecognition.parseSeasonNumber(
                anime.title,
                season.title,
                season.seasonNumber,
            )
            season = season.copy(seasonNumber = seasonNumber)

            val dbSeason = dbSeasons.find { it.anime.url == season.url }?.anime
            if (dbSeason == null) {
                newSeasons.add(season)
            } else {
                if (shouldUpdateDbSeason.await(dbSeason, season)) {
                    val toChangeSeason = dbSeason.copy(
                        title = season.title,
                        seasonNumber = season.seasonNumber,
                        seasonSourceOrder = season.seasonSourceOrder,
                    )
                    updatedSeasons.add(toChangeSeason)
                }
            }
        }

        // Return if there's nothing to add, delete, or update to avoid unnecessary db transactions.
        if (newSeasons.isEmpty() && removedSeasons.isEmpty() && updatedSeasons.isEmpty()) {
            if (manualFetch || anime.fetchInterval == 0 || anime.nextUpdate < fetchWindow.first) {
                updateAnime.awaitUpdateFetchInterval(
                    anime,
                    now,
                    fetchWindow,
                )
            }
            return sourceSeasons
        }

        if (removedSeasons.isNotEmpty()) {
            val toDeleteIds = removedSeasons.map { it.id }
            animeRepository.removeParentIdByIds(toDeleteIds)
        }

        val toUpdate = newSeasons.map { it.toAnimeUpdate() } +
            updatedSeasons.map { it.toAnimeUpdate() }

        if (toUpdate.isNotEmpty()) {
            updateAnime.awaitAll(toUpdate)
        }

        updateAnime.awaitUpdateLastUpdate(anime.id)

        return sourceSeasons
    }
}
