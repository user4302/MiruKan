package com.user4302.mika.domain.source.manga.interactor

import com.user4302.mika.domain.source.manga.repository.MangaSourceRepository
import com.user4302.mika.domain.source.manga.repository.SourcePagingSourceType
import com.user4302.mika.source.model.FilterList

class GetRemoteManga(
    private val repository: MangaSourceRepository,
) {

    fun subscribe(sourceId: Long, query: String, filterList: FilterList): SourcePagingSourceType {
        return when (query) {
            QUERY_POPULAR -> repository.getPopularManga(sourceId)
            QUERY_LATEST -> repository.getLatestManga(sourceId)
            else -> repository.searchManga(sourceId, query, filterList)
        }
    }

    companion object {
        const val QUERY_POPULAR = "com.user4302.domain.source.manga.interactor.POPULAR"
        const val QUERY_LATEST = "com.user4302.domain.source.manga.interactor.LATEST"
    }
}
