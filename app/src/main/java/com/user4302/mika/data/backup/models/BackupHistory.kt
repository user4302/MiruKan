package com.user4302.mika.data.backup.models

import com.user4302.mika.domain.history.manga.model.MangaHistory
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import java.util.Date

@Serializable
data class BackupHistory(
    @ProtoNumber(1) var url: String,
    @ProtoNumber(2) var lastRead: Long,
    @ProtoNumber(3) var readDuration: Long = 0,
) {
    fun getHistoryImpl(): MangaHistory {
        return MangaHistory.create().copy(
            readAt = Date(lastRead),
            readDuration = readDuration,
        )
    }
}
