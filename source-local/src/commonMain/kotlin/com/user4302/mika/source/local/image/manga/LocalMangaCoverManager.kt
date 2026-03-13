package com.user4302.mika.source.local.image.manga

import com.hippo.unifile.UniFile
import com.user4302.mika.source.model.SManga
import java.io.InputStream

expect class LocalMangaCoverManager {

    fun find(mangaUrl: String): UniFile?

    fun update(manga: SManga, inputStream: InputStream): UniFile?
}
