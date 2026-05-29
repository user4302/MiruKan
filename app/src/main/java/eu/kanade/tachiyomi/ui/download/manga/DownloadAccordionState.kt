package eu.kanade.tachiyomi.ui.download.manga

import eu.kanade.tachiyomi.ui.download.manga.MangaDownloadItem

/**
 * Represents the complete state of the download queue accordion UI for manga.
 */
data class DownloadAccordionState(
    val activeItem: MangaDownloadItem? = null,
    val pendingItems: List<MangaDownloadItem> = emptyList(),
    val completedItems: List<MangaDownloadItem> = emptyList(),
    val failedItems: List<MangaDownloadItem> = emptyList(),
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
