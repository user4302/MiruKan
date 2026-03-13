package com.user4302.domain.track.manga.interactor

import com.user4302.domain.track.manga.model.toDbTrack
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.data.track.EnhancedMangaTracker
import com.user4302.mika.data.track.MangaTracker
import com.user4302.mika.domain.items.chapter.interactor.GetChaptersByMangaId
import com.user4302.mika.domain.items.chapter.interactor.UpdateChapter
import com.user4302.mika.domain.items.chapter.model.toChapterUpdate
import com.user4302.mika.domain.track.manga.interactor.InsertMangaTrack
import com.user4302.mika.domain.track.manga.model.MangaTrack
import logcat.LogPriority
import kotlin.math.max

class SyncChapterProgressWithTrack(
    private val updateChapter: UpdateChapter,
    private val insertTrack: InsertMangaTrack,
    private val getChaptersByMangaId: GetChaptersByMangaId,
) {

    suspend fun await(
        mangaId: Long,
        remoteTrack: MangaTrack,
        tracker: MangaTracker,
    ) {
        if (tracker !is EnhancedMangaTracker) {
            return
        }

        val sortedChapters = getChaptersByMangaId.await(mangaId)
            .sortedBy { it.chapterNumber }
            .filter { it.isRecognizedNumber }

        val chapterUpdates = sortedChapters
            .filter { chapter -> chapter.chapterNumber <= remoteTrack.lastChapterRead && !chapter.read }
            .map { it.copy(read = true).toChapterUpdate() }

        // only take into account continuous reading
        val localLastRead = sortedChapters.takeWhile { it.read }.lastOrNull()?.chapterNumber ?: 0F
        val lastRead = max(remoteTrack.lastChapterRead, localLastRead.toDouble())
        val updatedTrack = remoteTrack.copy(lastChapterRead = lastRead)

        try {
            tracker.update(updatedTrack.toDbTrack())
            updateChapter.awaitAll(chapterUpdates)
            insertTrack.await(updatedTrack)
        } catch (e: Throwable) {
            logcat(LogPriority.WARN, e)
        }
    }
}
