# Implementation Plan: Download Queue Accordions

**Branch**: `001-download-queue-accordion` | **Date**: 2026-05-25 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/001-download-queue-accordion/spec.md`

## Summary

Implement a three-accordion system for the download queue interface that separates downloads into Pending, Completed, and Failed sections. The active downloading item remains visible at the top, while completed/failed items transition from active to their respective accordions. The implementation requires state management changes in both MangaDownloadQueueScreenModel and AnimeDownloadQueueScreenModel, with confirmation dialogs for destructive "Clear All" actions.

## Technical Context

**Language/Version**: Kotlin (JVM target), Android API 21+

**Primary Dependencies**: 
- Voyager (ScreenModel, Screen)
- FlexibleAdapter (RecyclerView adapter with accordion support)
- Kotlin Coroutines + StateFlow
- AndroidX Compose (for UI components)

**Storage**: In-memory StateFlow (queue state managed by MangaDownloadManager/AnimeDownloadManager)

**Testing**: Unit tests via JUnit, Android instrumentation tests

**Target Platform**: Android (mobile)

**Project Type**: Android mobile application (fork of Aniyomi/Mihon)

**Performance Goals**: 
- 60 FPS UI interactions
- <100ms state transition response
- <10MB memory overhead for download queue

**Constraints**: 
- Offline-first architecture (per Constitution Principle II)
- Battery efficiency (per Constitution Principle IV)
- Testable code structure (per Constitution Principle III)

**Scale/Scope**: 
- Supports unlimited download queue items
- Two separate implementations (manga + anime)
- Multiple device screen sizes

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Check | Status |
|-----------|-------|--------|
| I. User Experience First | Accordion UI provides clear visual separation and intuitive interaction patterns | ✅ PASS |
| II. Offline-First Architecture | Download state persists locally; no network required for queue management | ✅ PASS |
| III. Testable Code Structure | State management separated from UI; ScreenModel pattern enables unit testing | ✅ PASS |
| IV. Performance & Battery Efficiency | StateFlow-based reactive updates minimize unnecessary recompositions | ✅ PASS |
| V. Data Privacy & Security | No user data transmitted; all state local to device | ✅ PASS |

## Project Structure

### Documentation (this feature)

```text
specs/001-download-queue-accordion/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
│   └── state-contract.md
└── tasks.md             # Phase 2 output (via /speckit.tasks)
```

### Source Code (repository root)

```text
app/src/main/java/eu/kanade/tachiyomi/ui/download/
├── manga/
│   ├── MangaDownloadQueueScreenModel.kt  # Modified
│   ├── MangaDownloadQueueScreen.kt       # Modified
│   ├── MangaDownloadItem.kt              # Modified
│   └── MangaDownloadAdapter.kt           # Modified
└── anime/
    ├── AnimeDownloadQueueScreenModel.kt  # Modified
    ├── AnimeDownloadQueueScreen.kt       # Modified
    ├── AnimeDownloadItem.kt              # Modified
    └── AnimeDownloadAdapter.kt           # Modified
```

**Structure Decision**: Single project approach - modifications to existing download queue screens in both manga and anime modules. The accordion UI will be implemented using existing FlexibleAdapter patterns with custom header items for collapsible sections.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Dual implementation (manga + anime) | Both content types have separate download managers and UI | Code sharing would require significant refactoring of existing architecture |
| StateFlow for accordion state | Enables reactive UI updates and testability | Simple boolean flags would not support complex state transitions |

## Research Findings

### Current Architecture Analysis

1. **MangaDownloadQueueScreenModel** (`MangaDownloadQueueScreenModel.kt:27-260`)
   - Uses `MutableStateFlow<List<MangaDownloadItem>>` for state
   - Maps `downloadManager.queueState` to items with `isActive` flag
   - First item (index 0) is marked as active

2. **MangaDownloadItem** (`MangaDownloadItem.kt:11-72`)
   - Wraps `MangaDownload` with `isActive` boolean
   - `isDraggable()` returns false for active items
   - Uses different layouts for active vs pending items

3. **MangaDownload.State** (`MangaDownload.kt:67-73`)
   - States: NOT_DOWNLOADED, QUEUE, DOWNLOADING, DOWNLOADED, ERROR

4. **MangaDownloadManager** (`MangaDownloadManager.kt:64-65`)
   - `queueState` is the source of truth for download queue
   - `clearQueue()` clears all items

### Key Implementation Challenges

1. **State Separation**: Need to track three separate lists (pending, completed, failed) while maintaining the single queue from the download manager
2. **Active Item Transitions**: When active item completes, it must move to completed list
3. **Confirmation Dialogs**: Need to integrate with existing dialog patterns in the codebase
4. **Dual Implementation**: Both manga and anime modules need parallel changes

### Recommended Approach

1. Create `DownloadAccordionState` data class to hold the three lists
2. Modify ScreenModel to compute accordion state from `queueState`
3. Add `clearPending()`, `clearCompleted()`, `clearFailed()` methods
4. Implement confirmation dialog using existing `AlertDialog` patterns
5. Create accordion header items for the RecyclerView

## Data Model Design

### New Data Classes

```kotlin
// DownloadAccordionState.kt
data class DownloadAccordionState(
    val activeItem: MangaDownloadItem?,
    val pendingItems: List<MangaDownloadItem> = emptyList(),
    val completedItems: List<MangaDownloadItem> = emptyList(),
    val failedItems: List<MangaDownloadItem> = emptyList(),
)

// AccordionHeaderItem.kt (for RecyclerView)
data class AccordionHeaderItem(
    val title: String,
    val count: Int,
    val isExpanded: Boolean,
    val type: AccordionType,
)

enum class AccordionType { PENDING, COMPLETED, FAILED }
```

### State Transitions

| Current State | Event | Next State |
|---------------|-------|------------|
| Active (DOWNLOADING) | Completes | Active → Completed |
| Active (DOWNLOADING) | Error | Active → Failed |
| Active (PAUSED) | N/A | Stays Active |
| Pending | Started | Removed from Pending |
| Completed | Clear All | List cleared |
| Failed | Clear All | List cleared |

## Interface Contracts

### ScreenModel Interface

```kotlin
// MangaDownloadQueueScreenModel
val accordionState: StateFlow<DownloadAccordionState>
fun toggleAccordion(type: AccordionType)
fun clearPending() // with confirmation
fun clearCompleted() // with confirmation
fun clearFailed() // with confirmation
```

### UI Events

```kotlin
sealed class DownloadQueueEvent {
    data class ShowConfirmDialog(val type: AccordionType) : DownloadQueueEvent()
    data object DismissDialog : DownloadQueueEvent()
}
```

## Quickstart Guide

### Phase 1: Core State Management
1. Create `DownloadAccordionState` data class
2. Modify `MangaDownloadQueueScreenModel` to compute accordion state
3. Add `clearPending()`, `clearCompleted()`, `clearFailed()` methods
4. Repeat for `AnimeDownloadQueueScreenModel`

### Phase 2: UI Implementation
1. Create `AccordionHeaderItem` for RecyclerView
2. Modify `MangaDownloadAdapter` to support sections
3. Update `MangaDownloadQueueScreen` to display accordions
4. Add confirmation dialog integration

### Phase 3: Testing & Polish
1. Unit tests for state transitions
2. UI tests for accordion interactions
3. Performance testing with large queues
