
package mika.domain.source.anime.repository

import com.user4302.mika.domain.source.anime.model.StubAnimeSource
import kotlinx.coroutines.flow.Flow

interface AnimeStubSourceRepository {
    fun subscribeAllAnime(): Flow<List<StubAnimeSource>>

    suspend fun getStubAnimeSource(id: Long): StubAnimeSource?

    suspend fun upsertStubAnimeSource(id: Long, lang: String, name: String)
}
