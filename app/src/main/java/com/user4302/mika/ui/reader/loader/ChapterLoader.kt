package com.user4302.mika.ui.reader.loader

import android.content.Context
import com.user4302.mika.core.common.i18n.stringResource
import com.user4302.mika.core.common.util.lang.withIOContext
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.data.download.manga.MangaDownloadManager
import com.user4302.mika.data.download.manga.MangaDownloadProvider
import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.domain.source.manga.model.StubMangaSource
import com.user4302.mika.i18n.MR
import com.user4302.mika.source.MangaSource
import com.user4302.mika.source.local.entries.manga.LocalMangaSource
import com.user4302.mika.source.local.io.Format
import com.user4302.mika.source.online.HttpSource
import com.user4302.mika.ui.reader.model.ReaderChapter
import com.user4302.mika.ui.reader.setting.ReaderPreferences
import mihon.core.archive.archiveReader
import mihon.core.archive.epubReader
import uy.kohesive.injekt.injectLazy

/**
 * Loader used to retrieve the [PageLoader] for a given chapter.
 */
class ChapterLoader(
    private val context: Context,
    private val downloadManager: MangaDownloadManager,
    private val downloadProvider: MangaDownloadProvider,
    private val manga: Manga,
    private val source: MangaSource,
) {

    private val readerPreferences: ReaderPreferences by injectLazy()

    /**
     * Assigns the chapter's page loader and loads the its pages. Returns immediately if the chapter
     * is already loaded.
     */
    suspend fun loadChapter(chapter: ReaderChapter) {
        if (chapterIsReady(chapter)) {
            return
        }

        chapter.state = ReaderChapter.State.Loading
        withIOContext {
            logcat { "Loading pages for ${chapter.chapter.name}" }
            try {
                val loader = getPageLoader(chapter)
                chapter.pageLoader = loader

                val pages = loader.getPages()
                    .onEach { it.chapter = chapter }
                if (pages.isEmpty()) {
                    throw Exception(context.stringResource(MR.strings.page_list_empty_error))
                }

                // If the chapter is partially read, set the starting page to the last the user read
                // otherwise use the requested page.
                if (!chapter.chapter.read || readerPreferences.preserveReadingPosition().get()) {
                    chapter.requestedPage = chapter.chapter.last_page_read
                }

                chapter.state = ReaderChapter.State.Loaded(pages)
            } catch (e: Throwable) {
                chapter.state = ReaderChapter.State.Error(e)
                throw e
            }
        }
    }

    /**
     * Checks [chapter] to be loaded based on present pages and loader in addition to state.
     */
    private fun chapterIsReady(chapter: ReaderChapter): Boolean {
        return chapter.state is ReaderChapter.State.Loaded && chapter.pageLoader != null
    }

    /**
     * Returns the page loader to use for this [chapter].
     */
    private fun getPageLoader(chapter: ReaderChapter): PageLoader {
        val dbChapter = chapter.chapter
        val isDownloaded = downloadManager.isChapterDownloaded(
            dbChapter.name,
            dbChapter.scanlator,
            manga.title,
            manga.source,
            skipCache = true,
        )
        return when {
            isDownloaded -> DownloadPageLoader(
                chapter,
                manga,
                source,
                downloadManager,
                downloadProvider,
            )
            source is LocalMangaSource -> source.getFormat(chapter.chapter).let { format ->
                when (format) {
                    is Format.Directory -> DirectoryPageLoader(format.file)
                    is Format.Archive -> ArchivePageLoader(format.file.archiveReader(context))
                    is Format.Epub -> EpubPageLoader(format.file.epubReader(context))
                }
            }
            source is HttpSource -> HttpPageLoader(chapter, source)
            source is StubMangaSource -> error(
                context.stringResource(MR.strings.source_not_installed, source.toString()),
            )
            else -> error(context.stringResource(MR.strings.loader_not_implemented_error))
        }
    }
}
