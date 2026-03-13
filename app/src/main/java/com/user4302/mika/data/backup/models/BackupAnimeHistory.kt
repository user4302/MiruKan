package com.user4302.mika.data.backup.models

import com.user4302.mika.domain.history.anime.model.AnimeHistory
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import java.util.Date

@Serializable
data class BackupAnimeHistory(
    @ProtoNumber(1) var url: String,
    @ProtoNumber(2) var lastRead: Long,
    @ProtoNumber(3) var readDuration: Long = 0,
) {
    fun getHistoryImpl(): AnimeHistory {
        return AnimeHistory.create().copy(
            seenAt = Date(lastRead),
        )
    }
}
