package com.user4302.mika.data.track.mangaupdates.dto

import kotlinx.serialization.Serializable

@Serializable
data class MUStatus(
    val volume: Int? = null,
    val chapter: Int? = null,
)
