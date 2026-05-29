package eu.kanade.tachiyomi.ui.download.manga

import eu.kanade.tachiyomi.data.download.manga.model.MangaDownload
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

    companion object {
        fun fromItems(
            items: List<MangaDownloadItem>,
            completedItems: List<MangaDownloadItem> = emptyList(),
        ): DownloadAccordionState {
            val activeItem = items.firstOrNull { it.isActive }
            val pendingItems = items.filter {
                !it.isActive && it.download.status.let { status ->
                    status == MangaDownload.State.QUEUE || status == MangaDownload.State.NOT_DOWNLOADED
                }
            }
            val allCompletedItems = (items.filter {
                !it.isActive && it.download.status == MangaDownload.State.DOWNLOADED
            } + completedItems).distinctBy { it.download.chapter.id }
            val failedItems = items.filter {
                !it.isActive && it.download.status == MangaDownload.State.ERROR
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
