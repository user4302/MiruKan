package com.user4302.mika.data.track.simkl.dto

import kotlinx.serialization.Serializable

@Serializable
data class SimklUser(
    val account: SimklUserAccount,
)

@Serializable
data class SimklUserAccount(
    val id: Int,
)
