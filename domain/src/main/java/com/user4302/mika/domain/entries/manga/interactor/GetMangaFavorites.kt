package com.user4302.mika.domain.entries.manga.interactor

import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.domain.entries.manga.repository.MangaRepository
import kotlinx.coroutines.flow.Flow

class GetMangaFavorites(
    private val mangaRepository: MangaRepository,
) {

    suspend fun await(): List<Manga> {
        return mangaRepository.getMangaFavorites()
    }

    fun subscribe(sourceId: Long): Flow<List<Manga>> {
        return mangaRepository.getMangaFavoritesBySourceId(sourceId)
    }
}
