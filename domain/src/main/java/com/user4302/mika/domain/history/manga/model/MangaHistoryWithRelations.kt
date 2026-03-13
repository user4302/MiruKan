package com.user4302.mika.domain.history.manga.model

import com.user4302.mika.domain.entries.manga.model.MangaCover
import java.util.Date

data class MangaHistoryWithRelations(
    val id: Long,
    val chapterId: Long,
    val mangaId: Long,
    val title: String,
    val chapterNumber: Double,
    val readAt: Date?,
    val readDuration: Long,
    val coverData: MangaCover,
)
