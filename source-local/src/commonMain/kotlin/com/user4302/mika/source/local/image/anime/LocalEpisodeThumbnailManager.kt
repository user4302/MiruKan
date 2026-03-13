package com.user4302.mika.source.local.image.anime

import com.hippo.unifile.UniFile
import com.user4302.mika.animesource.model.SAnime
import com.user4302.mika.animesource.model.SEpisode
import java.io.InputStream

expect class LocalEpisodeThumbnailManager {

    fun find(animeUrl: String, fileName: String): UniFile?

    fun update(anime: SAnime, episode: SEpisode, inputStream: InputStream): UniFile?
}
