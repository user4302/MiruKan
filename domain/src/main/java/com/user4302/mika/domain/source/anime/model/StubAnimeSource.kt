package com.user4302.mika.domain.source.anime.model

import com.user4302.mika.animesource.AnimeSource
import com.user4302.mika.animesource.model.SAnime
import com.user4302.mika.animesource.model.SEpisode
import com.user4302.mika.animesource.model.Video

@Suppress("OverridingDeprecatedMember")
class StubAnimeSource(
    override val id: Long,
    override val lang: String,
    override val name: String,
) : AnimeSource {

    private val isInvalid: Boolean = name.isBlank() || lang.isBlank()

    override suspend fun getAnimeDetails(anime: SAnime): SAnime =
        throw AnimeSourceNotInstalledException()

    override suspend fun getEpisodeList(anime: SAnime): List<SEpisode> =
        throw AnimeSourceNotInstalledException()

    override suspend fun getSeasonList(anime: SAnime): List<SAnime> =
        throw AnimeSourceNotInstalledException()

    override suspend fun getVideoList(episode: SEpisode): List<Video> =
        throw AnimeSourceNotInstalledException()

    override fun toString(): String =
        if (!isInvalid) "$name (${lang.uppercase()})" else id.toString()

    companion object {
        fun from(source: AnimeSource): StubAnimeSource {
            return StubAnimeSource(id = source.id, lang = source.lang, name = source.name)
        }
    }
}
class AnimeSourceNotInstalledException : Exception()
