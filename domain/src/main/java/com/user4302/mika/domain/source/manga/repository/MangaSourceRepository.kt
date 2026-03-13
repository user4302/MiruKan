package com.user4302.mika.domain.source.manga.repository

import androidx.paging.PagingSource
import com.user4302.mika.domain.source.manga.model.MangaSourceWithCount
import com.user4302.mika.domain.source.manga.model.Source
import com.user4302.mika.source.model.FilterList
import com.user4302.mika.source.model.SManga
import kotlinx.coroutines.flow.Flow

typealias SourcePagingSourceType = PagingSource<Long, SManga>

interface MangaSourceRepository {

    fun getMangaSources(): Flow<List<Source>>

    fun getOnlineMangaSources(): Flow<List<Source>>

    fun getMangaSourcesWithFavoriteCount(): Flow<List<Pair<Source, Long>>>

    fun getMangaSourcesWithNonLibraryManga(): Flow<List<MangaSourceWithCount>>

    fun searchManga(sourceId: Long, query: String, filterList: FilterList): SourcePagingSourceType

    fun getPopularManga(sourceId: Long): SourcePagingSourceType

    fun getLatestManga(sourceId: Long): SourcePagingSourceType
}
