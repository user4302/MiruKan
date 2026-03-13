package com.user4302.mika.source.local.entries.anime

import android.content.Context
import com.user4302.mika.animesource.model.FetchType
import com.user4302.mika.source.local.io.ArchiveAnime
import com.user4302.mika.source.local.io.anime.LocalAnimeSourceFileSystem

actual class LocalAnimeFetchTypeManager(
    private val context: Context,
    private val fileSystem: LocalAnimeSourceFileSystem,
) {
    actual fun find(animeUrl: String): FetchType {
        val files = fileSystem.getFilesInAnimeDirectory(animeUrl)

        return when {
            files.any { ArchiveAnime.isSupported(it) } -> FetchType.Episodes
            files.any { it.isDirectory } -> FetchType.Seasons
            else -> FetchType.Episodes
        }
    }
}
