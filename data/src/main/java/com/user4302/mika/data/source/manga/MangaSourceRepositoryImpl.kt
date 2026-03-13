package com.user4302.mika.data.source.manga

import com.user4302.mika.data.handlers.manga.MangaDatabaseHandler
import com.user4302.mika.domain.source.manga.model.MangaSourceWithCount
import com.user4302.mika.domain.source.manga.model.StubMangaSource
import com.user4302.mika.domain.source.manga.repository.MangaSourceRepository
import com.user4302.mika.domain.source.manga.repository.SourcePagingSourceType
import com.user4302.mika.domain.source.manga.service.MangaSourceManager
import com.user4302.mika.source.CatalogueSource
import com.user4302.mika.source.MangaSource
import com.user4302.mika.source.model.FilterList
import com.user4302.mika.source.online.HttpSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import com.user4302.mika.domain.source.manga.model.Source as DomainSource

class MangaSourceRepositoryImpl(
    private val sourceManager: MangaSourceManager,
    private val handler: MangaDatabaseHandler,
) : MangaSourceRepository {

    override fun getMangaSources(): Flow<List<DomainSource>> {
        return sourceManager.catalogueSources.map { sources ->
            sources.map {
                mapSourceToDomainSource(it).copy(
                    supportsLatest = it.supportsLatest,
                )
            }
        }
    }

    override fun getOnlineMangaSources(): Flow<List<DomainSource>> {
        return sourceManager.catalogueSources.map { sources ->
            sources
                .filterIsInstance<HttpSource>()
                .map(::mapSourceToDomainSource)
        }
    }

    override fun getMangaSourcesWithFavoriteCount(): Flow<List<Pair<DomainSource, Long>>> {
        return combine(
            handler.subscribeToList { mangasQueries.getSourceIdWithFavoriteCount() },
            sourceManager.catalogueSources,
        ) { sourceIdWithFavoriteCount, _ -> sourceIdWithFavoriteCount }
            .map {
                it.map { (sourceId, count) ->
                    val source = sourceManager.getOrStub(sourceId)
                    val domainSource = mapSourceToDomainSource(source).copy(
                        isStub = source is StubMangaSource,
                    )
                    domainSource to count
                }
            }
    }

    override fun getMangaSourcesWithNonLibraryManga(): Flow<List<MangaSourceWithCount>> {
        val sourceIdWithNonLibraryManga =
            handler.subscribeToList { mangasQueries.getSourceIdsWithNonLibraryManga() }
        return sourceIdWithNonLibraryManga.map { sourceId ->
            sourceId.map { (sourceId, count) ->
                val source = sourceManager.getOrStub(sourceId)
                val domainSource = mapSourceToDomainSource(source).copy(
                    isStub = source is StubMangaSource,
                )
                MangaSourceWithCount(domainSource, count)
            }
        }
    }

    override fun searchManga(
        sourceId: Long,
        query: String,
        filterList: FilterList,
    ): SourcePagingSourceType {
        val source = sourceManager.get(sourceId) as CatalogueSource
        return SourceSearchPagingSource(source, query, filterList)
    }

    override fun getPopularManga(sourceId: Long): SourcePagingSourceType {
        val source = sourceManager.get(sourceId) as CatalogueSource
        return SourcePopularPagingSource(source)
    }

    override fun getLatestManga(sourceId: Long): SourcePagingSourceType {
        val source = sourceManager.get(sourceId) as CatalogueSource
        return SourceLatestPagingSource(source)
    }

    private fun mapSourceToDomainSource(source: MangaSource): DomainSource = DomainSource(
        id = source.id,
        lang = source.lang,
        name = source.name,
        supportsLatest = false,
        isStub = false,
    )
}
