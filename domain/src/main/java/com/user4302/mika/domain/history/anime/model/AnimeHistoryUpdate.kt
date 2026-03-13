package com.user4302.mika.domain.history.anime.model

import java.util.Date

data class AnimeHistoryUpdate(
    val episodeId: Long,
    val seenAt: Date,
)
