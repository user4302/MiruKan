package eu.kanade.tachiyomi.ui.download.anime

import eu.kanade.tachiyomi.data.download.anime.model.AnimeDownload
import eu.kanade.tachiyomi.ui.download.anime.AnimeDownloadItem

/**
 * Represents the complete state of the download queue accordion UI for anime.
 */
data class DownloadAccordionState(
    val activeItem: AnimeDownloadItem? = null,
    val pendingItems: List<AnimeDownloadItem> = emptyList(),
    val completedItems: List<AnimeDownloadItem> = emptyList(),
    val failedItems: List<AnimeDownloadItem> = emptyList(),
    val pendingExpanded: Boolean = true,
    val completedExpanded: Boolean = false,
    val failedExpanded: Boolean = false,
) {
    val hasAnyItems: Boolean
        get() = activeItem != null || pendingItems.isNotEmpty() ||
                completedItems.isNotEmpty() || failedItems.isNotEmpty()

    val pendingCount: Int
        get() = pendingItems.size

    val completedCount: Int
        get() = completedItems.size

    val failedCount: Int
        get() = failedItems.size

    companion object {
        fun fromItems(
            items: List<AnimeDownloadItem>,
            completedItems: List<AnimeDownloadItem> = emptyList(),
        ): DownloadAccordionState {
            val activeItem = items.firstOrNull { it.isActive }
            val pendingItems = items.filter {
                !it.isActive && it.download.status.let { status ->
                    status == AnimeDownload.State.QUEUE || status == AnimeDownload.State.NOT_DOWNLOADED
                }
            }
            val allCompletedItems = (items.filter {
                !it.isActive && it.download.status == AnimeDownload.State.DOWNLOADED
            } + completedItems).distinctBy { it.download.episode.id }
            val failedItems = items.filter {
                !it.isActive && it.download.status == AnimeDownload.State.ERROR
            }

            return DownloadAccordionState(
                activeItem = activeItem,
                pendingItems = pendingItems,
                completedItems = allCompletedItems,
                failedItems = failedItems,
            )
        }
    }
}
