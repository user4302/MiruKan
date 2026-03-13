package com.user4302.mika.domain.source.anime.repository

import androidx.paging.PagingSource
import com.user4302.mika.animesource.model.AnimeFilterList
import com.user4302.mika.animesource.model.SAnime
import com.user4302.mika.domain.source.anime.model.AnimeSource
import kotlinx.coroutines.flow.Flow

typealias AnimeSourcePagingSourceType = PagingSource<Long, SAnime>

interface AnimeSourceRepository {

    fun getAnimeSources(): Flow<List<AnimeSource>>

    fun getOnlineAnimeSources(): Flow<List<AnimeSource>>

    fun getAnimeSourcesWithFavoriteCount(): Flow<List<Pair<AnimeSource, Long>>>

    fun searchAnime(sourceId: Long, query: String, filterList: AnimeFilterList): AnimeSourcePagingSourceType

    fun getPopularAnime(sourceId: Long): AnimeSourcePagingSourceType

    fun getLatestAnime(sourceId: Long): AnimeSourcePagingSourceType
}
