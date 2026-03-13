package com.user4302.mika.domain.source.anime.interactor

import com.user4302.mika.animesource.model.AnimeFilterList
import com.user4302.mika.domain.source.anime.repository.AnimeSourcePagingSourceType
import com.user4302.mika.domain.source.anime.repository.AnimeSourceRepository

class GetRemoteAnime(
    private val repository: AnimeSourceRepository,
) {

    fun subscribe(sourceId: Long, query: String, filterList: AnimeFilterList): AnimeSourcePagingSourceType {
        return when (query) {
            QUERY_POPULAR -> repository.getPopularAnime(sourceId)
            QUERY_LATEST -> repository.getLatestAnime(sourceId)
            else -> repository.searchAnime(sourceId, query, filterList)
        }
    }

    companion object {
        const val QUERY_POPULAR = "com.user4302.domain.source.anime.interactor.POPULAR"
        const val QUERY_LATEST = "com.user4302.domain.source.anime.interactor.LATEST"
    }
}
