package com.user4302.mika.ui.reader.loader

import android.app.Application
import android.net.Uri
import com.hippo.unifile.UniFile
import com.user4302.mika.data.database.models.manga.toDomainChapter
import com.user4302.mika.data.download.manga.MangaDownloadManager
import com.user4302.mika.data.download.manga.MangaDownloadProvider
import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.source.MangaSource
import com.user4302.mika.source.model.Page
import com.user4302.mika.ui.reader.model.ReaderChapter
import com.user4302.mika.ui.reader.model.ReaderPage
import mihon.core.archive.archiveReader
import uy.kohesive.injekt.injectLazy

/**
 * Loader used to load a chapter from the downloaded chapters.
 */
internal class DownloadPageLoader(
    private val chapter: ReaderChapter,
    private val manga: Manga,
    private val source: MangaSource,
    private val downloadManager: MangaDownloadManager,
    private val downloadProvider: MangaDownloadProvider,
) : PageLoader() {

    private val context: Application by injectLazy()

    private var archivePageLoader: ArchivePageLoader? = null

    override var isLocal: Boolean = true

    override suspend fun getPages(): List<ReaderPage> {
        val dbChapter = chapter.chapter
        val chapterPath = downloadProvider.findChapterDir(
            dbChapter.name,
            dbChapter.scanlator,
            manga.title,
            source,
        )
        return if (chapterPath?.isFile == true) {
            getPagesFromArchive(chapterPath)
        } else {
            getPagesFromDirectory()
        }
    }

    override fun recycle() {
        super.recycle()
        archivePageLoader?.recycle()
    }

    private suspend fun getPagesFromArchive(file: UniFile): List<ReaderPage> {
        val loader = ArchivePageLoader(file.archiveReader(context)).also { archivePageLoader = it }
        return loader.getPages()
    }

    private fun getPagesFromDirectory(): List<ReaderPage> {
        val pages = downloadManager.buildPageList(
            source,
            manga,
            chapter.chapter.toDomainChapter()!!,
        )
        return pages.map { page ->
            ReaderPage(page.index, page.url, page.imageUrl) {
                context.contentResolver.openInputStream(page.uri ?: Uri.EMPTY)!!
            }.apply {
                status = Page.State.READY
            }
        }
    }

    override suspend fun loadPage(page: ReaderPage) {
        archivePageLoader?.loadPage(page)
    }
}
