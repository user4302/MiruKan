package com.user4302.mika.source.local.image.anime

import com.hippo.unifile.UniFile
import com.user4302.mika.animesource.model.SAnime
import java.io.InputStream

expect class LocalAnimeBackgroundManager {

    fun find(animeUrl: String): UniFile?

    fun update(anime: SAnime, inputStream: InputStream): UniFile?
}
