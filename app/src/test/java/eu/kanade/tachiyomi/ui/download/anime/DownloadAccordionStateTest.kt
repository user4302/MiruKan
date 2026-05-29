package eu.kanade.tachiyomi.ui.download.anime

import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import eu.kanade.tachiyomi.data.download.anime.model.AnimeDownload
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import tachiyomi.domain.entries.anime.model.Anime
import tachiyomi.domain.items.episode.model.Episode

class DownloadAccordionStateTest {

    @Test
    fun `active item is selected from queue`() {
        val activeDownload = createAnimeDownload(1, AnimeDownload.State.DOWNLOADING)
        val pendingDownload = createAnimeDownload(2, AnimeDownload.State.QUEUE)
        val state = DownloadAccordionState.fromItems(
            listOf(
                AnimeDownloadItem(activeDownload, true),
                AnimeDownloadItem(pendingDownload, false),
            ),
        )

        assertNotNull(state.activeItem)
        assertSame(activeDownload, state.activeItem?.download)
        assertEquals(1, state.pendingCount)
        assertEquals(0, state.completedCount)
        assertEquals(0, state.failedCount)
    }

    private fun createAnimeDownload(episodeId: Long, status: AnimeDownload.State): AnimeDownload {
        val source = mockk<AnimeHttpSource>(relaxed = true) {
            every { name } returns "Test"
            every { lang } returns "en"
            every { baseUrl } returns "https://example.com"
            every { versionId } returns 1
        }

        return AnimeDownload(
            source = source,
            anime = Anime.create().copy(id = 1L, title = "Test Anime", source = 1L),
            episode = Episode.create().copy(id = episodeId, animeId = 1L, name = "Episode $episodeId"),
        ).apply {
            this.status = status
        }
    }
}
