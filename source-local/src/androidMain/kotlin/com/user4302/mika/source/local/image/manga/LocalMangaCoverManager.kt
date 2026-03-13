package com.user4302.mika.source.local.image.manga

import android.content.Context
import com.hippo.unifile.UniFile
import com.user4302.mika.core.common.storage.nameWithoutExtension
import com.user4302.mika.core.common.util.system.ImageUtil
import com.user4302.mika.source.local.io.manga.LocalMangaSourceFileSystem
import com.user4302.mika.source.model.SManga
import com.user4302.mika.util.storage.DiskUtil
import java.io.InputStream

private const val DEFAULT_COVER_NAME = "cover.jpg"

actual class LocalMangaCoverManager(
    private val context: Context,
    private val fileSystem: LocalMangaSourceFileSystem,
) {

    actual fun find(mangaUrl: String): UniFile? {
        return fileSystem.getFilesInMangaDirectory(mangaUrl)
            // Get all file whose names start with "cover"
            .filter { it.isFile && it.nameWithoutExtension.equals("cover", ignoreCase = true) }
            // Get the first actual image
            .firstOrNull { ImageUtil.isImage(it.name) { it.openInputStream() } }
    }

    actual fun update(
        manga: SManga,
        inputStream: InputStream,
    ): UniFile? {
        val directory = fileSystem.getMangaDirectory(manga.url)
        if (directory == null) {
            inputStream.close()
            return null
        }

        val targetFile = find(manga.url) ?: directory.createFile(DEFAULT_COVER_NAME)!!

        inputStream.use { input ->
            targetFile.openOutputStream().use { output ->
                input.copyTo(output)
            }
        }

        DiskUtil.createNoMediaFile(directory, context)

        manga.thumbnail_url = targetFile.uri.toString()
        return targetFile
    }
}
