package com.user4302.mika.domain.entries.manga.interactor

import com.user4302.mika.domain.entries.manga.repository.MangaRepository

class ResetMangaViewerFlags(
    private val mangaRepository: MangaRepository,
) {

    suspend fun await(): Boolean {
        return mangaRepository.resetMangaViewerFlags()
    }
}
