package eu.kanade.tachiyomi.ui.download.manga

import eu.kanade.tachiyomi.data.download.manga.model.MangaDownload
import eu.kanade.tachiyomi.source.online.HttpSource
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import tachiyomi.domain.entries.manga.model.Manga
import tachiyomi.domain.items.chapter.model.Chapter

class DownloadAccordionStateTest {

    @Test
    fun `active item is selected from queue`() {
        val activeDownload = createMangaDownload(1, MangaDownload.State.DOWNLOADING)
        val pendingDownload = createMangaDownload(2, MangaDownload.State.QUEUE)
        val state = DownloadAccordionState.fromItems(
            listOf(
                MangaDownloadItem(activeDownload, true),
                MangaDownloadItem(pendingDownload, false),
            ),
        )

        assertNotNull(state.activeItem)
        assertSame(activeDownload, state.activeItem?.download)
        assertEquals(1, state.pendingCount)
        assertEquals(0, state.completedCount)
        assertEquals(0, state.failedCount)
    }

    private fun createMangaDownload(chapterId: Long, status: MangaDownload.State): MangaDownload {
        val source = mockk<HttpSource>(relaxed = true) {
            every { name } returns "Test"
            every { lang } returns "en"
            every { baseUrl } returns "https://example.com"
            every { versionId } returns 1
        }

        return MangaDownload(
            source = source,
            manga = Manga.create().copy(id = 1L, title = "Test Manga", source = 1L),
            chapter = Chapter.create().copy(id = chapterId, mangaId = 1L, name = "Chapter $chapterId"),
        ).apply {
            this.status = status
        }
    }
}
