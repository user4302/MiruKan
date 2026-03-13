package com.user4302.domain.extension.anime.model

import com.user4302.mika.extension.anime.model.AnimeExtension

data class AnimeExtensions(
    val updates: List<AnimeExtension.Installed>,
    val installed: List<AnimeExtension.Installed>,
    val available: List<AnimeExtension.Available>,
    val untrusted: List<AnimeExtension.Untrusted>,
)
