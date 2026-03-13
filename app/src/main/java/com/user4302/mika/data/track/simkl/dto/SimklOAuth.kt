package com.user4302.mika.data.track.simkl.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimklOAuth(
    @SerialName("access_token")
    val accessToken: String,
)
