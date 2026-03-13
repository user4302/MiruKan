package com.user4302.mika.domain.source.anime.model

import com.user4302.mika.animesource.model.FetchType

data class DeletableAnime(
    val animeId: Long,
    val sourceId: Long,
    val fetchType: FetchType,
)
