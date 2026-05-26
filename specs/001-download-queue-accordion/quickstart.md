# Quickstart: Download Queue Accordions

**Date**: 2026-05-25

## Prerequisites

- Android Studio (latest stable)
- Android SDK 21+
- Project built with Gradle

## Development Setup

```bash
# Clone and setup
git clone <repo-url>
cd MiruKan
./gradlew assembleDebug
```

## Implementation Steps

### Step 1: Create Data Models

Create `DownloadAccordionState.kt` in both manga and anime download packages:

```kotlin
// app/src/main/java/eu/kanade/tachiyomi/ui/download/manga/DownloadAccordionState.kt
data class DownloadAccordionState(
    val activeItem: MangaDownloadItem? = null,
    val pendingItems: List<MangaDownloadItem> = emptyList(),
    val completedItems: List<MangaDownloadItem> = emptyList(),
    val failedItems: List<MangaDownloadItem> = emptyList(),
)
```

### Step 2: Modify ScreenModel

Update `MangaDownloadQueueScreenModel.kt`:

1. Add `accordionState` StateFlow
2. Add `computeAccordionState()` method
3. Add `clearPending()`, `clearCompleted()`, `clearFailed()` methods
4. Add confirmation dialog state

### Step 3: Create Accordion Header

Create `AccordionHeaderItem.kt` and `AccordionHeaderHolder.kt`:

```kotlin
// AccordionHeaderItem.kt
class AccordionHeaderItem(
    val type: AccordionType,
    val title: String,
    val count: Int,
    val isExpanded: Boolean = true,
) : AbstractFlexibleItem<AccordionHeaderHolder>()
```

### Step 4: Update Adapter

Modify `MangaDownloadAdapter.kt` to:
1. Support mixed item types (header + download items)
2. Handle accordion expand/collapse
3. Show count badges

### Step 5: Update UI

Modify `MangaDownloadQueueScreen.kt` to:
1. Observe `accordionState` instead of `state`
2. Display active item separately
3. Render accordion sections

### Step 6: Add Confirmation Dialog

Add dialog state to ScreenModel and handle in UI:

```kotlin
// In ScreenModel
private val _showConfirmDialog = MutableStateFlow<AccordionType?>(null)
val showConfirmDialog = _showConfirmDialog.asStateFlow()

fun onClearAllClicked(type: AccordionType) {
    _showConfirmDialog.value = type
}

fun confirmClear() {
    when (_showConfirmDialog.value) {
        AccordionType.PENDING -> clearPending()
        AccordionType.COMPLETED -> clearCompleted()
        AccordionType.FAILED -> clearFailed()
        null -> {}
    }
    _showConfirmDialog.value = null
}
```

## Testing

```bash
# Run unit tests
./gradlew testDebugUnitTest

# Run instrumentation tests
./gradlew connectedDebugAndroidTest
```

## Build & Verify

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

## Key Files to Modify

| File | Changes |
|------|---------|
| `MangaDownloadQueueScreenModel.kt` | Add accordion state, clear methods |
| `MangaDownloadQueueScreen.kt` | Update UI to use accordion state |
| `MangaDownloadAdapter.kt` | Support header items |
| `MangaDownloadItem.kt` | May need state transition handling |
| `AnimeDownloadQueueScreenModel.kt` | Same changes as manga |
| `AnimeDownloadQueueScreen.kt` | Same changes as manga |
| `AnimeDownloadAdapter.kt` | Same changes as manga |

## Common Pitfalls

1. **State synchronization**: Ensure accordion state updates when download status changes
2. **Empty sections**: Hide accordions when count is 0
3. **Active item transitions**: Handle DOWNLOADED/ERROR state changes properly
4. **Dual implementation**: Remember to update both manga and anime versions