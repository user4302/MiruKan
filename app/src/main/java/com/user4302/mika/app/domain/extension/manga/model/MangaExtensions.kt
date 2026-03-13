package com.user4302.domain.extension.manga.model

import com.user4302.mika.extension.manga.model.MangaExtension

data class MangaExtensions(
    val updates: List<MangaExtension.Installed>,
    val installed: List<MangaExtension.Installed>,
    val available: List<MangaExtension.Available>,
    val untrusted: List<MangaExtension.Untrusted>,
)
