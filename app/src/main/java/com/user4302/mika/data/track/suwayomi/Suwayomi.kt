package com.user4302.mika.data.track.suwayomi

import android.graphics.Color
import com.user4302.mika.R
import com.user4302.mika.data.database.models.manga.MangaTrack
import com.user4302.mika.data.track.BaseTracker
import com.user4302.mika.data.track.EnhancedMangaTracker
import com.user4302.mika.data.track.MangaTracker
import com.user4302.mika.data.track.model.MangaTrackSearch
import com.user4302.mika.i18n.MR
import com.user4302.mika.source.MangaSource
import dev.icerock.moko.resources.StringResource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import com.user4302.mika.domain.entries.manga.model.Manga as DomainManga
import com.user4302.mika.domain.track.manga.model.MangaTrack as DomainTrack

class Suwayomi(id: Long) : BaseTracker(id, "Suwayomi"), EnhancedMangaTracker, MangaTracker {

    val api by lazy { SuwayomiApi(id) }

    override fun getLogo() = R.drawable.ic_tracker_suwayomi

    override fun getLogoColor() = Color.rgb(255, 35, 35) // TODO

    companion object {
        const val UNREAD = 1L
        const val READING = 2L
        const val COMPLETED = 3L
    }

    override fun getStatusListManga(): List<Long> = listOf(UNREAD, READING, COMPLETED)

    override fun getStatusForManga(status: Long): StringResource? = when (status) {
        UNREAD -> MR.strings.unread
        READING -> MR.strings.reading
        COMPLETED -> MR.strings.completed
        else -> null
    }

    override fun getReadingStatus(): Long = READING

    override fun getRereadingStatus(): Long = -1

    override fun getCompletionStatus(): Long = COMPLETED

    override fun getScoreList(): ImmutableList<String> = persistentListOf()

    override fun displayScore(track: DomainTrack): String = ""

    override suspend fun update(track: MangaTrack, didReadChapter: Boolean): MangaTrack {
        if (track.status != COMPLETED) {
            if (didReadChapter) {
                if (track.last_chapter_read.toLong() == track.total_chapters && track.total_chapters > 0) {
                    track.status = COMPLETED
                } else {
                    track.status = READING
                }
            }
        }

        return api.updateProgress(track)
    }

    override suspend fun bind(track: MangaTrack, hasReadChapters: Boolean): MangaTrack {
        return track
    }

    override suspend fun searchManga(query: String): List<MangaTrackSearch> {
        TODO("Not yet implemented")
    }

    override suspend fun refresh(track: MangaTrack): MangaTrack {
        val remoteTrack = api.getTrackSearch(track.tracking_url)
        track.copyPersonalFrom(remoteTrack)
        track.total_chapters = remoteTrack.total_chapters
        return track
    }

    override suspend fun login(username: String, password: String) {
        saveCredentials("user", "pass")
    }

    override fun loginNoop() {
        saveCredentials("user", "pass")
    }

    override fun getAcceptedSources(): List<String> = listOf(
        "com.user4302.mika.extension.all.tachidesk.Tachidesk",
    )

    override suspend fun match(manga: DomainManga): MangaTrackSearch? =
        try {
            api.getTrackSearch(manga.url)
        } catch (e: Exception) {
            null
        }

    override fun isTrackFrom(track: DomainTrack, manga: DomainManga, source: MangaSource?): Boolean = source?.let {
        accept(
            it,
        )
    } == true

    override fun migrateTrack(track: DomainTrack, manga: DomainManga, newSource: MangaSource): DomainTrack? =
        if (accept(newSource)) {
            track.copy(remoteUrl = manga.url)
        } else {
            null
        }
}
