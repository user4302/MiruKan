package eu.kanade.tachiyomi.ui.download.anime

sealed class DownloadQueueEvent {
    data class ShowClearConfirmDialog(val type: AccordionType) : DownloadQueueEvent()
}
