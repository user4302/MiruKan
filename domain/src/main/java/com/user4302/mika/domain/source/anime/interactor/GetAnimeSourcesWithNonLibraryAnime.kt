package com.user4302.mika.domain.source.anime.interactor

import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.domain.entries.anime.repository.AnimeRepository
import com.user4302.mika.domain.source.anime.model.DeletableAnime
import kotlinx.coroutines.flow.Flow

class GetAnimeSourcesWithNonLibraryAnime(
    private val repository: AnimeRepository,
) {

    fun subscribe(): Flow<List<DeletableAnime>> {
        return repository.getDeletableParentAnime()
    }

    suspend fun getDeletableChildren(parentId: Long): List<Anime> {
        return repository.getChildrenByParentId(parentId)
    }
}
