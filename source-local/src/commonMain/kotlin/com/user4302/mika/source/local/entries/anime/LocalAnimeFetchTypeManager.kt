package com.user4302.mika.source.local.entries.anime

import com.user4302.mika.animesource.model.FetchType

expect class LocalAnimeFetchTypeManager {
    fun find(animeUrl: String): FetchType
}
