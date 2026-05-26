# Research: Download Queue Accordions

**Date**: 2026-05-25

## Current Architecture Analysis

### Manga Download Queue

**File**: `app/src/main/java/eu/kanade/tachiyomi/ui/download/manga/MangaDownloadQueueScreenModel.kt`

The current implementation uses a single `MutableStateFlow<List<MangaDownloadItem>>` that maps directly from `downloadManager.queueState`. The first item in the list is marked as `isActive = true`.

**Key observations**:
- State is managed via `MutableStateFlow` (line 31)
- Active item detection: `index == 0` in the map (line 117)
- `isDraggable()` returns false for active items (line 57-59 in MangaDownloadItem.kt)
- Download states: NOT_DOWNLOADED, QUEUE, DOWNLOADING, DOWNLOADED, ERROR

### Anime Download Queue

**File**: `app/src/main/java/eu/kanade/tachiyomi/ui/download/anime/AnimeDownloadQueueScreenModel.kt`

Identical structure to manga, with `AnimeDownload` and `AnimeDownloadItem` types.

### Download Manager

**File**: `app/src/main/java/eu/kanade/tachiyomi/data/download/manga/MangaDownloadManager.kt`

- `queueState` is a `MutableStateFlow<List<MangaDownload>>` (exposed via downloader)
- `clearQueue()` clears all items
- `statusFlow()` and `progressFlow()` emit status changes

## Accordion UI Patterns in Codebase

### Existing Accordion Usage

The codebase uses `FlexibleAdapter` which supports:
- `AbstractFlexibleItem` for list items
- `IFlexible` interface for item behavior
- Header items for section separation

**Reference**: `MangaDownloadHeaderItem.kt` and `MangaDownloadHeaderHolder.kt` exist for section headers.

### Confirmation Dialog Patterns

**File**: `app/src/main/java/eu/kanade/tachiyomi/ui/category/manga/MangaCategoryScreenModel.kt`

```kotlin
data class Delete(val category: Category) : MangaCategoryDialog
```

Dialog patterns use sealed classes for dialog state, with `ScreenModel` managing dialog visibility.

## Technical Constraints

1. **Voyager ScreenModel**: State must be exposed as `StateFlow` for Compose integration
2. **RecyclerView**: Must use `FlexibleAdapter` for list management
3. **Dual Implementation**: Changes needed in both manga and anime modules
4. **Offline-First**: All state must be local, no network dependencies

## Recommended Implementation Path

1. **State Layer**: Create `DownloadAccordionState` to separate pending/completed/failed
2. **ScreenModel**: Compute accordion state from `queueState` with proper filtering
3. **UI Layer**: Use `FlexibleAdapter` with header items for accordion sections
4. **Confirmation**: Use existing dialog patterns with sealed class state