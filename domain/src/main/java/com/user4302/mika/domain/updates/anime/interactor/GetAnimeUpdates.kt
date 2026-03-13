package com.user4302.mika.domain.updates.anime.interactor

import com.user4302.mika.domain.updates.anime.model.AnimeUpdatesWithRelations
import com.user4302.mika.domain.updates.anime.repository.AnimeUpdatesRepository
import kotlinx.coroutines.flow.Flow
import java.time.Instant

class GetAnimeUpdates(
    private val repository: AnimeUpdatesRepository,
) {

    suspend fun await(seen: Boolean, after: Long): List<AnimeUpdatesWithRelations> {
        return repository.awaitWithSeen(seen, after, limit = 500)
    }

    fun subscribe(instant: Instant): Flow<List<AnimeUpdatesWithRelations>> {
        return repository.subscribeAllAnimeUpdates(instant.toEpochMilli(), limit = 500)
    }

    fun subscribe(seen: Boolean, after: Long): Flow<List<AnimeUpdatesWithRelations>> {
        return repository.subscribeWithSeen(seen, after, limit = 500)
    }
}
