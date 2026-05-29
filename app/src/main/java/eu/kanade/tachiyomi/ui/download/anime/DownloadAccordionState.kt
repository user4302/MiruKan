package eu.kanade.tachiyomi.ui.download.anime

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
}
