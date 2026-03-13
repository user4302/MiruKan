package com.user4302.mika.domain.updates.manga.repository

import com.user4302.mika.domain.updates.manga.model.MangaUpdatesWithRelations
import kotlinx.coroutines.flow.Flow

interface MangaUpdatesRepository {

    suspend fun awaitWithRead(read: Boolean, after: Long, limit: Long): List<MangaUpdatesWithRelations>

    fun subscribeAllMangaUpdates(after: Long, limit: Long): Flow<List<MangaUpdatesWithRelations>>

    fun subscribeWithRead(read: Boolean, after: Long, limit: Long): Flow<List<MangaUpdatesWithRelations>>
}
