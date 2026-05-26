# Data Model: Download Queue Accordions

**Date**: 2026-05-25

## Core Data Structures

### DownloadAccordionState

```kotlin
package eu.kanade.tachiyomi.ui.download.manga

import kotlinx.coroutines.flow.StateFlow

/**
 * Represents the complete state of the download queue accordion UI.
 * Separates downloads into three distinct categories for display.
 */
data class DownloadAccordionState(
    val activeItem: MangaDownloadItem? = null,
    val pendingItems: List<MangaDownloadItem> = emptyList(),
    val completedItems: List<MangaDownloadItem> = emptyList(),
    val failedItems: List<MangaDownloadItem> = emptyList(),
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
```

### AccordionHeaderItem

```kotlin
package eu.kanade.tachiyomi.ui.download.manga

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem

/**
 * Header item for accordion sections in the RecyclerView.
 * Displays title, count badge, and expand/collapse state.
 */
data class AccordionHeaderItem(
    val type: AccordionType,
    val title: String,
    val count: Int,
    val isExpanded: Boolean = true,
) : AbstractFlexibleItem<AccordionHeaderHolder>() {
    
    override fun getLayoutRes(): Int = R.layout.download_accordion_header
    
    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
    ): AccordionHeaderHolder {
        return AccordionHeaderHolder(view, adapter as MangaDownloadAdapter)
    }
    
    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: AccordionHeaderHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        holder.bind(this)
    }
    
    override fun isDraggable(): Boolean = false
    override fun isSwipeable(): Boolean = false
}

enum class AccordionType {
    PENDING, COMPLETED, FAILED
}
```

### DownloadQueueEvent

```kotlin
package eu.kanade.tachiyomi.ui.download.manga

/**
 * UI events for the download queue screen.
 * Used for one-time actions like showing dialogs.
 */
sealed class DownloadQueueEvent {
    data class ShowClearConfirmDialog(val type: AccordionType) : DownloadQueueEvent()
    data object DismissDialog : DownloadQueueEvent()
    data class ToggleAccordion(val type: AccordionType) : DownloadQueueEvent()
}
```

## State Transitions

### Download State to Accordion Mapping

| MangaDownload.State | Accordion Section | Notes |
|---------------------|-------------------|-------|
| DOWNLOADING | Active (top) | Only one active at a time |
| QUEUE | Pending | Waiting in queue |
| DOWNLOADED | Completed | Successfully finished |
| ERROR | Failed | Failed with error |
| NOT_DOWNLOADED | Pending | Not yet started |

### State Transition Diagram

```
[Pending] --start--> [Active/DOWNLOADING] --complete--> [Completed]
                               |
                               +--error--> [Failed]
                               |
                               +--cancel--> [Pending] (removed)
```

## ScreenModel State Flow

```kotlin
class MangaDownloadQueueScreenModel(...) : ScreenModel {
    // Original state (kept for compatibility)
    private val _state = MutableStateFlow(emptyList<MangaDownloadItem>())
    val state = _state.asStateFlow()
    
    // New accordion state
    private val _accordionState = MutableStateFlow(DownloadAccordionState())
    val accordionState = _accordionState.asStateFlow()
    
    // Dialog state
    private val _event = MutableStateFlow<DownloadQueueEvent?>(null)
    val event = _event.asStateFlow()
    
    init {
        screenModelScope.launch {
            downloadManager.queueState
                .map { downloads ->
                    downloads.mapIndexed { index, download ->
                        MangaDownloadItem(download, index == 0)
                    }
                }
                .collect { items ->
                    _state.update { items }
                    _accordionState.update { computeAccordionState(items) }
                }
        }
    }
    
    private fun computeAccordionState(items: List<MangaDownloadItem>): DownloadAccordionState {
        val activeItem = items.firstOrNull { it.isActive }
        val pendingItems = items.filter { 
            !it.isActive && it.download.status == MangaDownload.State.QUEUE 
        }
        val completedItems = items.filter { 
            it.download.status == MangaDownload.State.DOWNLOADED 
        }
        val failedItems = items.filter { 
            it.download.status == MangaDownload.State.ERROR 
        }
        
        return DownloadAccordionState(
            activeItem = activeItem,
            pendingItems = pendingItems,
            completedItems = completedItems,
            failedItems = failedItems,
        )
    }
}
```

## Adapter Structure

The `MangaDownloadAdapter` will need to support mixed item types:

```kotlin
class MangaDownloadAdapter(
    listener: DownloadItemListener,
) : FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>(null, listener, true) {
    
    // Items are ordered: [ActiveItem], [PendingHeader, PendingItems...], 
    //                   [CompletedHeader, CompletedItems...], [FailedHeader, FailedItems...]
}