package com.user4302.mika.domain.entries.manga.interactor

import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.domain.entries.manga.repository.MangaRepository

class GetDuplicateLibraryManga(
    private val mangaRepository: MangaRepository,
) {

    suspend fun await(manga: Manga): List<Manga> {
        return mangaRepository.getDuplicateLibraryManga(manga.id, manga.title.lowercase())
    }
}
