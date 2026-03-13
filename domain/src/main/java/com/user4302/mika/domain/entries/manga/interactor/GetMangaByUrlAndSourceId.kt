package com.user4302.mika.domain.entries.manga.interactor

import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.domain.entries.manga.repository.MangaRepository

class GetMangaByUrlAndSourceId(
    private val mangaRepository: MangaRepository,
) {
    suspend fun await(url: String, sourceId: Long): Manga? {
        return mangaRepository.getMangaByUrlAndSourceId(url, sourceId)
    }
}
