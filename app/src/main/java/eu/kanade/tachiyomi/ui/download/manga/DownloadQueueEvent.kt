package eu.kanade.tachiyomi.ui.download.manga

sealed class DownloadQueueEvent {
    data class ShowClearConfirmDialog(val type: AccordionType) : DownloadQueueEvent()
}
