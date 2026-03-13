package com.user4302.mika.data.source.manga

import com.user4302.mika.data.handlers.manga.MangaDatabaseHandler
import com.user4302.mika.domain.source.manga.model.StubMangaSource
import com.user4302.mika.domain.source.manga.repository.MangaStubSourceRepository
import kotlinx.coroutines.flow.Flow

class MangaStubSourceRepositoryImpl(
    private val handler: MangaDatabaseHandler,
) : MangaStubSourceRepository {

    override fun subscribeAllManga(): Flow<List<StubMangaSource>> {
        return handler.subscribeToList { sourcesQueries.findAll(::mapStubSource) }
    }

    override suspend fun getStubMangaSource(id: Long): StubMangaSource? {
        return handler.awaitOneOrNull {
            sourcesQueries.findOne(
                id,
                ::mapStubSource,
            )
        }
    }

    override suspend fun upsertStubMangaSource(id: Long, lang: String, name: String) {
        handler.await { sourcesQueries.upsert(id, lang, name) }
    }

    private fun mapStubSource(
        id: Long,
        lang: String,
        name: String,
    ): StubMangaSource = StubMangaSource(id = id, lang = lang, name = name)
}
