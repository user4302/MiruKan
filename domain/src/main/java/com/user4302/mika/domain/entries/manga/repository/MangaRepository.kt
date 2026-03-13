package com.user4302.mika.domain.entries.manga.repository

import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.domain.entries.manga.model.MangaUpdate
import com.user4302.mika.domain.library.manga.LibraryManga
import kotlinx.coroutines.flow.Flow

interface MangaRepository {

    suspend fun getMangaById(id: Long): Manga

    suspend fun getMangaByIdAsFlow(id: Long): Flow<Manga>

    suspend fun getMangaByUrlAndSourceId(url: String, sourceId: Long): Manga?

    fun getMangaByUrlAndSourceIdAsFlow(url: String, sourceId: Long): Flow<Manga?>

    suspend fun getMangaFavorites(): List<Manga>

    suspend fun getReadMangaNotInLibrary(): List<Manga>

    suspend fun getLibraryManga(): List<LibraryManga>

    fun getLibraryMangaAsFlow(): Flow<List<LibraryManga>>

    fun getMangaFavoritesBySourceId(sourceId: Long): Flow<List<Manga>>

    suspend fun getDuplicateLibraryManga(id: Long, title: String): List<Manga>

    suspend fun getUpcomingManga(statuses: Set<Long>): Flow<List<Manga>>

    suspend fun resetMangaViewerFlags(): Boolean

    suspend fun setMangaCategories(mangaId: Long, categoryIds: List<Long>)

    suspend fun insertManga(manga: Manga): Long?

    suspend fun updateManga(update: MangaUpdate): Boolean

    suspend fun updateAllManga(mangaUpdates: List<MangaUpdate>): Boolean
}
