package com.user4302.mika.domain.source.manga.repository

import com.user4302.mika.domain.source.manga.model.StubMangaSource
import kotlinx.coroutines.flow.Flow

interface MangaStubSourceRepository {
    fun subscribeAllManga(): Flow<List<StubMangaSource>>

    suspend fun getStubMangaSource(id: Long): StubMangaSource?

    suspend fun upsertStubMangaSource(id: Long, lang: String, name: String)
}
