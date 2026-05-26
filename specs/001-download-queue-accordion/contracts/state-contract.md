# State Contract: Download Queue Accordions

**Date**: 2026-05-25

## State Machine Definition

### DownloadAccordionState

```
State: DownloadAccordionState
{
  activeItem: MangaDownloadItem?     // Currently downloading (DOWNLOADING state)
  pendingItems: List<MangaDownloadItem>  // QUEUE state items
  completedItems: List<MangaDownloadItem> // DOWNLOADED state items
  failedItems: List<MangaDownloadItem>   // ERROR state items
}
```

## State Transitions

### Event: Download Started
- **Precondition**: Item exists in pendingItems
- **Action**: Move item to activeItem
- **Postcondition**: activeItem = item, pendingItems = pendingItems - item

### Event: Download Completed
- **Precondition**: activeItem.status == DOWNLOADING
- **Action**: Move activeItem to completedItems
- **Postcondition**: activeItem = null, completedItems = completedItems + item

### Event: Download Failed
- **Precondition**: activeItem.status == DOWNLOADING
- **Action**: Move activeItem to failedItems
- **Postcondition**: activeItem = null, failedItems = failedItems + item

### Event: Clear Pending
- **Precondition**: User confirmed clear action
- **Action**: Clear all pending items
- **Postcondition**: pendingItems = emptyList

### Event: Clear Completed
- **Precondition**: User confirmed clear action
- **Action**: Clear all completed items
- **Postcondition**: completedItems = emptyList

### Event: Clear Failed
- **Precondition**: User confirmed clear action
- **Action**: Clear all failed items
- **Postcondition**: failedItems = emptyList

## Invariants

1. **Single Active**: At most one item can be in activeItem at any time
2. **No Duplicates**: An item cannot exist in multiple sections simultaneously
3. **State Consistency**: Item status must match its section:
   - activeItem: DOWNLOADING
   - pendingItems: QUEUE
   - completedItems: DOWNLOADED
   - failedItems: ERROR

## UI State Mapping

| State | UI Behavior |
|-------|-------------|
| activeItem != null | Show active item at top with progress bar |
| pendingItems.isNotEmpty() | Show "Pending Items" accordion with count badge |
| completedItems.isNotEmpty() | Show "Completed Items" accordion with count badge |
| failedItems.isNotEmpty() | Show "Failed Items" accordion with count badge |
| All empty | Show "No downloads" empty screen |

## Dialog State Contract

```
State: ClearConfirmDialog
{
  visible: Boolean
  targetType: AccordionType?  // PENDING, COMPLETED, or FAILED
}
```

### Event: Show Clear Dialog
- **Precondition**: User taps "Clear All" on accordion
- **Action**: Set visible = true, targetType = clicked type
- **Postcondition**: Dialog displayed

### Event: Confirm Clear
- **Precondition**: Dialog visible, targetType set
- **Action**: Execute clear for targetType, dismiss dialog
- **Postcondition**: Items cleared, dialog hidden

### Event: Cancel Clear
- **Precondition**: Dialog visible
- **Action**: Dismiss dialog
- **Postcondition**: No state changes, dialog hidden