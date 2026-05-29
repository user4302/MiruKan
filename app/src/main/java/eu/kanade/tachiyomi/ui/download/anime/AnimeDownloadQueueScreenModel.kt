package eu.kanade.tachiyomi.ui.download.anime

import android.view.MenuItem
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.download.anime.AnimeDownloadManager
import eu.kanade.tachiyomi.data.download.anime.model.AnimeDownload
import eu.kanade.tachiyomi.databinding.DownloadListBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import eu.kanade.tachiyomi.ui.download.anime.AccordionType

class AnimeDownloadQueueScreenModel(
    private val downloadManager: AnimeDownloadManager = Injekt.get(),
) : ScreenModel {

    private val _state = MutableStateFlow(emptyList<AnimeDownloadItem>())
    val state = _state.asStateFlow()

    private val _completedItems = MutableStateFlow(emptyList<AnimeDownloadItem>())
    private val completedItems = _completedItems.asStateFlow()

    private val _accordionState = MutableStateFlow(DownloadAccordionState())
    val accordionState: StateFlow<DownloadAccordionState> = _accordionState.asStateFlow()

    private val _event = MutableStateFlow<DownloadQueueEvent?>(null)
    val event: StateFlow<DownloadQueueEvent?> = _event.asStateFlow()

    lateinit var controllerBinding: DownloadListBinding

    /**
     * Adapter containing the active downloads.
     */
    var adapter: AnimeDownloadAdapter? = null

    /**
     * Map of jobs for active downloads.
     */
    private val progressJobs = mutableMapOf<AnimeDownload, Job>()

    val listener = object : AnimeDownloadAdapter.DownloadItemListener {
        /**
         * Called when an item is released from a drag.
         *
         * @param position The position of the released item.
         */
        override fun onItemReleased(position: Int) {
            val adapter = adapter ?: return
            val downloads = adapter.currentItems.filterIsInstance<AnimeDownloadItem>().map { it.download }
            reorder(downloads)
        }

        /**
         * Called when the menu item of a download is pressed
         *
         * @param position The position of the item
         * @param menuItem The menu Item pressed
         */
        override fun onMenuItemClick(position: Int, menuItem: MenuItem) {
            val item = adapter?.getItem(position) ?: return
            if (item is AnimeDownloadItem) {
                when (menuItem.itemId) {
                    R.id.move_to_top, R.id.move_to_bottom -> {
                        val items =
                            adapter?.currentItems?.filterIsInstance<AnimeDownloadItem>()?.toMutableList() ?: return
                        val index = items.indexOf(item)
                        if (index != -1) {
                            items.removeAt(index)
                            if (menuItem.itemId == R.id.move_to_top) {
                                val targetIndex = if (items.isNotEmpty() && !items[0].isDraggable) 1 else 0
                                items.add(targetIndex, item)
                            } else {
                                items.add(item)
                            }
                            reorder(items.map { it.download })
                        }
                    }
                    R.id.move_to_top_series, R.id.move_to_bottom_series -> {
                        val (selectedSeries, otherSeries) = adapter?.currentItems
                            ?.filterIsInstance<AnimeDownloadItem>()
                            ?.map(AnimeDownloadItem::download)
                            ?.partition { item.download.anime.id == it.anime.id }
                            ?: Pair(emptyList(), emptyList())
                        if (menuItem.itemId == R.id.move_to_top_series) {
                            reorder(selectedSeries + otherSeries)
                        } else {
                            reorder(otherSeries + selectedSeries)
                        }
                    }
                    R.id.cancel_download -> {
                        cancel(listOf(item.download))
                    }
                    R.id.cancel_series -> {
                        val allAnimeDownloadsForSeries = adapter?.currentItems
                            ?.filterIsInstance<AnimeDownloadItem>()
                            ?.filter { item.download.anime.id == it.download.anime.id }
                            ?.map(AnimeDownloadItem::download)
                        if (!allAnimeDownloadsForSeries.isNullOrEmpty()) {
                            cancel(allAnimeDownloadsForSeries)
                        }
                    }
                }
            }
        }
        override fun onHeaderToggle(type: AccordionType) {
            toggleAccordion(type)
        }

        override fun onHeaderClear(type: AccordionType) {
            _event.value = DownloadQueueEvent.ShowClearConfirmDialog(type)
        }
    }

    init {
        screenModelScope.launch {
            downloadManager.queueState
                .map { downloads ->
                    downloads.mapIndexed { index, download ->
                        AnimeDownloadItem(download, index == 0)
                    }
                }
                .collect { newList ->
                    _state.update { newList }
                    _accordionState.update { computeAccordionState(newList) }
                }
        }
    }

    private fun computeAccordionState(items: List<AnimeDownloadItem>): DownloadAccordionState {
        val activeItem = items.firstOrNull { it.isActive }
        val pendingItems = items.filter {
            !it.isActive && it.download.status.let { status ->
                status == AnimeDownload.State.QUEUE || status == AnimeDownload.State.NOT_DOWNLOADED
            }
        }
        val completedItems = (items.filter {
            !it.isActive && it.download.status == AnimeDownload.State.DOWNLOADED
        } + _completedItems.value).distinctBy { it.download.episode.id }

        val failedItems = items.filter {
            !it.isActive && it.download.status == AnimeDownload.State.ERROR
        }

        return DownloadAccordionState(
            activeItem = activeItem,
            pendingItems = pendingItems,
            completedItems = completedItems,
            failedItems = failedItems,
        )
    }

    fun toggleAccordion(type: AccordionType) {
        _accordionState.update { currentState ->
            when (type) {
                AccordionType.PENDING -> currentState.copy(pendingExpanded = !currentState.pendingExpanded)
                AccordionType.COMPLETED -> currentState.copy(completedExpanded = !currentState.completedExpanded)
                AccordionType.FAILED -> currentState.copy(failedExpanded = !currentState.failedExpanded)
            }
        }
    }

    fun confirmClear(type: AccordionType) {
        when (type) {
            AccordionType.PENDING -> clearPending()
            AccordionType.COMPLETED -> clearCompleted()
            AccordionType.FAILED -> clearFailed()
        }
        _event.value = null
    }

    fun dismissDialog() {
        _event.value = null
    }

    private fun clearPending() {
        val pendingDownloads = _state.value.filter {
            !it.isActive && (it.download.status == AnimeDownload.State.QUEUE || it.download.status == AnimeDownload.State.NOT_DOWNLOADED)
        }.map { it.download }
        if (pendingDownloads.isNotEmpty()) {
            cancel(pendingDownloads)
        }
    }

    private fun clearFailed() {
        val failedDownloads = _state.value.filter {
            it.download.status == AnimeDownload.State.ERROR
        }.map { it.download }
        if (failedDownloads.isNotEmpty()) {
            cancel(failedDownloads)
        }
    }

    private fun clearCompleted() {
        _completedItems.value = emptyList()
        _accordionState.update { computeAccordionState(_state.value) }
    }

    fun buildDownloadItems(state: DownloadAccordionState): List<AbstractFlexibleItem<*>> {
        val items = mutableListOf<AbstractFlexibleItem<*>>()
        state.activeItem?.let { items.add(it) }

        if (state.pendingCount > 0) {
            items.add(AccordionHeaderItem(
                AccordionType.PENDING,
                "Pending Items",
                state.pendingCount,
                state.pendingExpanded,
            ))
            if (state.pendingExpanded) {
                items.addAll(state.pendingItems)
            }
        }

        if (state.completedCount > 0) {
            items.add(AccordionHeaderItem(
                AccordionType.COMPLETED,
                "Completed Items",
                state.completedCount,
                state.completedExpanded,
            ))
            if (state.completedExpanded) {
                items.addAll(state.completedItems)
            }
        }

        if (state.failedCount > 0) {
            items.add(AccordionHeaderItem(
                AccordionType.FAILED,
                "Failed Items",
                state.failedCount,
                state.failedExpanded,
            ))
            if (state.failedExpanded) {
                items.addAll(state.failedItems)
            }
        }

        return items
    }

    override fun onDispose() {
        for (job in progressJobs.values) {
            job.cancel()
        }
        progressJobs.clear()
        adapter = null
    }

    val isDownloaderRunning = downloadManager.isDownloaderRunning
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun getDownloadStatusFlow() = downloadManager.statusFlow()
    fun getDownloadProgressFlow() = downloadManager.progressFlow()

    fun startDownloads() {
        downloadManager.startDownloads()
    }

    fun pauseDownloads() {
        downloadManager.pauseDownloads()
    }

    fun clearQueue() {
        downloadManager.clearQueue()
    }

    fun reorder(downloads: List<AnimeDownload>) {
        downloadManager.reorderQueue(downloads)
    }

    fun cancel(downloads: List<AnimeDownload>) {
        downloadManager.cancelQueuedDownloads(downloads)
    }

    fun <R : Comparable<R>> reorderQueue(
        selector: (AnimeDownloadItem) -> R,
        reverse: Boolean = false,
    ) {
        val adapter = adapter ?: return
        val items = adapter.currentItems.filterIsInstance<AnimeDownloadItem>().toMutableList()
        val downloadingItem = if (items.isNotEmpty() && !items[0].isDraggable) items.removeAt(0) else null
        items.sortWith(if (reverse) compareByDescending(selector) else compareBy(selector))
        if (downloadingItem != null) {
            items.add(0, downloadingItem)
        }
        reorder(items.map { it.download })
    }

    /**
     * Called when the status of a download changes.
     *
     * @param download the download whose status has changed.
     */
    fun onStatusChange(download: AnimeDownload) {
        when (download.status) {
            AnimeDownload.State.DOWNLOADING -> {
                // Initial update of the downloaded pages
                onUpdateProgress(download)
                onUpdateDownloadedPages(download)
            }
            AnimeDownload.State.DOWNLOADED -> {
                cancelProgressJob(download)
                if (_completedItems.value.none { it.download.episode.id == download.episode.id }) {
                    _completedItems.update { it + AnimeDownloadItem(download, false) }
                }
                onUpdateProgress(download)
                onUpdateDownloadedPages(download)
            }
            AnimeDownload.State.ERROR -> {
                cancelProgressJob(download)
            }
            else -> {}
        }
        _accordionState.update { computeAccordionState(_state.value) }
    }

    /**
     * Unsubscribes the given download from the progress subscriptions.
     *
     * @param download the download to unsubscribe.
     */
    private fun cancelProgressJob(download: AnimeDownload) {
        progressJobs.remove(download)?.cancel()
    }

    /**
     * Called when the progress of a download changes.
     *
     * @param download the download whose progress has changed.
     */
    private fun onUpdateProgress(download: AnimeDownload) {
        getHolder(download)?.notifyProgress()
        getHolder(download)?.notifyDownloadedPages()
    }

    /**
     * Called when a page of a download is downloaded.
     *
     * @param download the download whose page has been downloaded.
     */
    fun onUpdateDownloadedPages(download: AnimeDownload) {
        getHolder(download)?.notifyDownloadedPages()
        getHolder(download)?.notifyProgress()
    }

    /**
     * Returns the holder for the given download.
     *
     * @param download the download to find.
     * @return the holder of the download or null if it's not bound.
     */
    private fun getHolder(download: AnimeDownload): AnimeDownloadHolder? {
        return controllerBinding.root.findViewHolderForItemId(download.episode.id) as? AnimeDownloadHolder
    }
}
