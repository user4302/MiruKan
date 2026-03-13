package com.user4302.mika.domain.source.manga.interactor

import com.user4302.mika.domain.source.manga.model.MangaSourceWithCount
import com.user4302.mika.domain.source.manga.repository.MangaSourceRepository
import kotlinx.coroutines.flow.Flow

class GetMangaSourcesWithNonLibraryManga(
    private val repository: MangaSourceRepository,
) {

    fun subscribe(): Flow<List<MangaSourceWithCount>> {
        return repository.getMangaSourcesWithNonLibraryManga()
    }
}
