package com.user4302.mika.data.track.mangaupdates.dto

import com.user4302.mika.data.database.models.manga.MangaTrack
import com.user4302.mika.data.track.mangaupdates.MangaUpdates.Companion.READING_LIST
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MUListItem(
    val series: MUSeries? = null,
    @SerialName("list_id")
    val listId: Long? = null,
    val status: MUStatus? = null,
    val priority: Int? = null,
)

fun MUListItem.copyTo(track: MangaTrack): MangaTrack {
    return track.apply {
        this.status = listId ?: READING_LIST
        this.last_chapter_read = this@copyTo.status?.chapter?.toDouble() ?: 0.0
    }
}
