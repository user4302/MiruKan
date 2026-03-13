package com.user4302.domain.source.anime.model

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.user4302.mika.domain.source.anime.model.AnimeSource
import com.user4302.mika.extension.anime.AnimeExtensionManager
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

val AnimeSource.icon: ImageBitmap?
    get() {
        return Injekt.get<AnimeExtensionManager>().getAppIconForSource(id)
            ?.toBitmap()
            ?.asImageBitmap()
    }
