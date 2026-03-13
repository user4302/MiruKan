package com.user4302.mika.ui.deeplink.anime

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.user4302.domain.entries.anime.model.toDomainAnime
import com.user4302.domain.entries.anime.model.toSAnime
import com.user4302.domain.items.episode.interactor.SyncEpisodesWithSource
import com.user4302.mika.animesource.AnimeSource
import com.user4302.mika.animesource.model.SAnime
import com.user4302.mika.animesource.model.SEpisode
import com.user4302.mika.animesource.online.ResolvableAnimeSource
import com.user4302.mika.animesource.online.UriType
import com.user4302.mika.core.common.util.lang.launchIO
import com.user4302.mika.domain.entries.anime.interactor.GetAnimeByUrlAndSourceId
import com.user4302.mika.domain.entries.anime.interactor.NetworkToLocalAnime
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.domain.items.episode.interactor.GetEpisodeByUrlAndAnimeId
import com.user4302.mika.domain.items.episode.model.Episode
import com.user4302.mika.domain.source.anime.service.AnimeSourceManager
import kotlinx.coroutines.flow.update
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class DeepLinkAnimeScreenModel(
    query: String = "",
    private val sourceManager: AnimeSourceManager = Injekt.get(),
    private val networkToLocalAnime: NetworkToLocalAnime = Injekt.get(),
    private val getEpisodeByUrlAndAnimeId: GetEpisodeByUrlAndAnimeId = Injekt.get(),
    private val getAnimeByUrlAndSourceId: GetAnimeByUrlAndSourceId = Injekt.get(),
    private val syncEpisodesWithSource: SyncEpisodesWithSource = Injekt.get(),
) : StateScreenModel<DeepLinkAnimeScreenModel.State>(State.Loading) {

    init {
        screenModelScope.launchIO {
            val source = sourceManager.getCatalogueSources()
                .filterIsInstance<ResolvableAnimeSource>()
                .firstOrNull { it.getUriType(query) != UriType.Unknown }

            val anime = source?.getAnime(query)?.let {
                getAnimeFromSAnime(it, source.id)
            }

            val episode = if (source?.getUriType(query) == UriType.Episode && anime != null) {
                source.getEpisode(query)?.let { getEpisodeFromSEpisode(it, anime, source) }
            } else {
                null
            }

            mutableState.update {
                if (anime == null) {
                    State.NoResults
                } else {
                    if (episode == null) {
                        State.Result(anime)
                    } else {
                        State.Result(anime, episode.id)
                    }
                }
            }
        }
    }

    private suspend fun getEpisodeFromSEpisode(sEpisode: SEpisode, anime: Anime, source: AnimeSource): Episode? {
        val localEpisode = getEpisodeByUrlAndAnimeId.await(sEpisode.url, anime.id)

        return if (localEpisode == null) {
            val sourceEpisodes = source.getEpisodeList(anime.toSAnime())
            val newEpisodes = syncEpisodesWithSource.await(sourceEpisodes, anime, source, false)
            newEpisodes.find { it.url == sEpisode.url }
        } else {
            localEpisode
        }
    }

    private suspend fun getAnimeFromSAnime(sAnime: SAnime, sourceId: Long): Anime {
        return getAnimeByUrlAndSourceId.await(sAnime.url, sourceId)
            ?: networkToLocalAnime.await(sAnime.toDomainAnime(sourceId))
    }

    sealed interface State {
        @Immutable
        data object Loading : State

        @Immutable
        data object NoResults : State

        @Immutable
        data class Result(val anime: Anime, val episodeId: Long? = null) : State
    }
}
