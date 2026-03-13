package com.user4302.mika.core.metadata.mika

import kotlinx.serialization.Serializable

@Serializable
class AnimeDetails(
    val title: String? = null,
    val author: String? = null,
    val artist: String? = null,
    val description: String? = null,
    val genre: List<String>? = null,
    val status: Int? = null,
)
