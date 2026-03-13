package com.user4302.mika.data.track.simkl.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimklSyncWatched(
    val result: Boolean?,
    @SerialName("last_watched")
    val lastWatched: String?,
    val list: String?,
)
