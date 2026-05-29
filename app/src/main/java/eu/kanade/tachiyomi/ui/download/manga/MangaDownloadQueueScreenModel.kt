package eu.kanade.tachiyomi.ui.download.manga

import android.view.MenuItem
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.download.manga.MangaDownloadManager
import eu.kanade.tachiyomi.data.download.manga.model.MangaDownload
import eu.kanade.tachiyomi.databinding.DownloadListBinding
import eu.kanade.tachiyomi.source.model.Page
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.kanade.tachiyomi.ui.download.manga.AccordionType
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class MangaDownloadQueueScreenModel(
    private val downloadManager: MangaDownloadManager = Injekt.get(),
) : ScreenModel {

    private val _state = MutableStateFlow(emptyList<MangaDownloadItem>())
    val state = _state.asStateFlow()

    private val _completedItems = MutableStateFlow(emptyList<MangaDownloadItem>())

    private val completedItems = _completedItems.asStateFlow()

    private val _accordionState = MutableStateFlow(DownloadAccordionState())
    val accordionState: StateFlow<DownloadAccordionState> = _accordionState.asStateFlow()

    private val _event = MutableStateFlow<DownloadQueueEvent?>(null)
    val event: StateFlow<DownloadQueueEvent?> = _event.asStateFlow()

    lateinit var controllerBinding: DownloadListBinding

    /**
     * Adapter containing the active downloads.
     */
    var adapter: MangaDownloadAdapter? = null

    /**
     * Map of jobs for active downloads.
     */
    private val progressJobs = mutableMapOf<MangaDownload, Job>()

    val listener = object : MangaDownloadAdapter.DownloadItemListener {
        /**
         * Called when an item is released from a drag.
         *
         * @param position The position of the released item.
         */
        override fun onItemReleased(position: Int) {
            val adapter = adapter ?: return
            val downloads = adapter.currentItems.filterIsInstance<MangaDownloadItem>().map { it.download }
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
            if (item is MangaDownloadItem) {
                when (menuItem.itemId) {
                    R.id.move_to_top, R.id.move_to_bottom -> {
                        val items =
                            adapter?.currentItems?.filterIsInstance<MangaDownloadItem>()?.toMutableList() ?: return
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
                            ?.filterIsInstance<MangaDownloadItem>()
                            ?.map(MangaDownloadItem::download)
                            ?.partition { item.download.manga.id == it.manga.id }
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
                        val allDownloadsForSeries = adapter?.currentItems
                            ?.filterIsInstance<MangaDownloadItem>()
                            ?.filter { item.download.manga.id == it.download.manga.id }
                            ?.map(MangaDownloadItem::download)
                        if (!allDownloadsForSeries.isNullOrEmpty()) {
                            cancel(allDownloadsForSeries)
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
                        MangaDownloadItem(download, index == 0)
                    }
                }
                .collect { newList ->
                    _state.update { newList }
                    _accordionState.update { computeAccordionState(newList) }
                }
        }
    }

    private fun computeAccordionState(items: List<MangaDownloadItem>): DownloadAccordionState {
        val activeItem = items.firstOrNull { it.isActive }
        val pendingItems = items.filter {
            !it.isActive && it.download.status.let { status ->
                status == MangaDownload.State.QUEUE || status == MangaDownload.State.NOT_DOWNLOADED
            }
        }
        val completedItems = (items.filter {
            !it.isActive && it.download.status == MangaDownload.State.DOWNLOADED
        } + _completedItems.value).distinctBy { it.download.chapter.id }

        val failedItems = items.filter {
            !it.isActive && it.download.status == MangaDownload.State.ERROR
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
            !it.isActive && (it.download.status == MangaDownload.State.QUEUE || it.download.status == MangaDownload.State.NOT_DOWNLOADED)
        }.map { it.download }
        if (pendingDownloads.isNotEmpty()) {
            cancel(pendingDownloads)
        }
    }

    private fun clearFailed() {
        val failedDownloads = _state.value.filter {
            it.download.status == MangaDownload.State.ERROR
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

    fun reorder(downloads: List<MangaDownload>) {
        downloadManager.reorderQueue(downloads)
    }

    fun cancel(downloads: List<MangaDownload>) {
        downloadManager.cancelQueuedDownloads(downloads)
    }

    fun <R : Comparable<R>> reorderQueue(
        selector: (MangaDownloadItem) -> R,
        reverse: Boolean = false,
    ) {
        val adapter = adapter ?: return
        val items = adapter.currentItems.filterIsInstance<MangaDownloadItem>().toMutableList()
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
    fun onStatusChange(download: MangaDownload) {
        when (download.status) {
            MangaDownload.State.DOWNLOADING -> {
                launchProgressJob(download)
                // Initial update of the downloaded pages
                onUpdateProgress(download)
                onUpdateDownloadedPages(download)
            }
            MangaDownload.State.DOWNLOADED -> {
                cancelProgressJob(download)
                if (_completedItems.value.none { it.download.chapter.id == download.chapter.id }) {
                    _completedItems.update { it + MangaDownloadItem(download, false) }
                }
                onUpdateProgress(download)
                onUpdateDownloadedPages(download)
            }
            MangaDownload.State.ERROR -> {
                cancelProgressJob(download)
            }
            else -> {}
        }
        _accordionState.update { computeAccordionState(_state.value) }
    }

    /**
     * Observe the progress of a download and notify the view.
     *
     * @param download the download to observe its progress.
     */
    private fun launchProgressJob(download: MangaDownload) {
        val job = screenModelScope.launch {
            while (download.pages == null) {
                delay(50)
            }

            val progressFlows = download.pages!!.map(Page::progressFlow)
            combine(progressFlows, Array<Int>::sum)
                .distinctUntilChanged()
                .debounce(50)
                .collectLatest {
                    onUpdateProgress(download)
                }
        }

        // Avoid leaking jobs
        progressJobs.remove(download)?.cancel()

        progressJobs[download] = job
    }

    /**
     * Unsubscribes the given download from the progress subscriptions.
     *
     * @param download the download to unsubscribe.
     */
    private fun cancelProgressJob(download: MangaDownload) {
        progressJobs.remove(download)?.cancel()
    }

    /**
     * Called when the progress of a download changes.
     *
     * @param download the download whose progress has changed.
     */
    private fun onUpdateProgress(download: MangaDownload) {
        getHolder(download)?.notifyProgress()
    }

    /**
     * Called when a page of a download is downloaded.
     *
     * @param download the download whose page has been downloaded.
     */
    fun onUpdateDownloadedPages(download: MangaDownload) {
        getHolder(download)?.notifyDownloadedPages()
        onUpdateProgress(download)
    }

    /**
     * Returns the holder for the given download.
     *
     * @param download the download to find.
     * @return the holder of the download or null if it's not bound.
     */
    private fun getHolder(download: MangaDownload): MangaDownloadHolder? {
        return controllerBinding.root.findViewHolderForItemId(download.chapter.id) as? MangaDownloadHolder
    }
}
