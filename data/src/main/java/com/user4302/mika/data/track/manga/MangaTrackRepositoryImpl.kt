package com.user4302.mika.data.track.manga

import com.user4302.mika.data.handlers.manga.MangaDatabaseHandler
import com.user4302.mika.domain.track.manga.model.MangaTrack
import com.user4302.mika.domain.track.manga.repository.MangaTrackRepository
import kotlinx.coroutines.flow.Flow

class MangaTrackRepositoryImpl(
    private val handler: MangaDatabaseHandler,
) : MangaTrackRepository {

    override suspend fun getTrackByMangaId(id: Long): MangaTrack? {
        return handler.awaitOneOrNull {
            manga_syncQueries.getTrackById(id, MangaTrackMapper::mapTrack)
        }
    }

    override suspend fun getTracksByMangaId(mangaId: Long): List<MangaTrack> {
        return handler.awaitList {
            manga_syncQueries.getTracksByMangaId(mangaId, MangaTrackMapper::mapTrack)
        }
    }

    override fun getMangaTracksAsFlow(): Flow<List<MangaTrack>> {
        return handler.subscribeToList { manga_syncQueries.getTracks(MangaTrackMapper::mapTrack) }
    }

    override fun getTracksByMangaIdAsFlow(mangaId: Long): Flow<List<MangaTrack>> {
        return handler.subscribeToList {
            manga_syncQueries.getTracksByMangaId(mangaId, MangaTrackMapper::mapTrack)
        }
    }

    override suspend fun delete(mangaId: Long, trackerId: Long) {
        handler.await {
            manga_syncQueries.delete(
                mangaId = mangaId,
                syncId = trackerId,
            )
        }
    }

    override suspend fun insertManga(track: MangaTrack) {
        insertValues(track)
    }

    override suspend fun insertAllManga(tracks: List<MangaTrack>) {
        insertValues(*tracks.toTypedArray())
    }

    private suspend fun insertValues(vararg tracks: MangaTrack) {
        handler.await(inTransaction = true) {
            tracks.forEach { mangaTrack ->
                manga_syncQueries.insert(
                    mangaId = mangaTrack.mangaId,
                    syncId = mangaTrack.trackerId,
                    remoteId = mangaTrack.remoteId,
                    libraryId = mangaTrack.libraryId,
                    title = mangaTrack.title,
                    lastChapterRead = mangaTrack.lastChapterRead,
                    totalChapters = mangaTrack.totalChapters,
                    status = mangaTrack.status,
                    score = mangaTrack.score,
                    remoteUrl = mangaTrack.remoteUrl,
                    startDate = mangaTrack.startDate,
                    finishDate = mangaTrack.finishDate,
                    private = mangaTrack.private,
                )
            }
        }
    }
}
