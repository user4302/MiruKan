package com.user4302.mika.domain.updates.anime.repository

import com.user4302.mika.domain.updates.anime.model.AnimeUpdatesWithRelations
import kotlinx.coroutines.flow.Flow

interface AnimeUpdatesRepository {

    suspend fun awaitWithSeen(seen: Boolean, after: Long, limit: Long): List<AnimeUpdatesWithRelations>

    fun subscribeAllAnimeUpdates(after: Long, limit: Long): Flow<List<AnimeUpdatesWithRelations>>

    fun subscribeWithSeen(seen: Boolean, after: Long, limit: Long): Flow<List<AnimeUpdatesWithRelations>>
}
