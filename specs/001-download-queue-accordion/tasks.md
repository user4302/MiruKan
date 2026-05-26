# Tasks: Download Queue Accordions

**Input**: Design documents from `/specs/001-download-queue-accordion/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Tests are included to ensure testable code structure per Constitution Principle III.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Mobile (Android)**: `app/src/main/java/eu/kanade/tachiyomi/ui/download/`
- Paths shown below are for the Android project structure

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create `DownloadAccordionState.kt` data class in `app/src/main/java/eu/kanade/tachiyomi/ui/download/manga/`
- [ ] T002 Create `DownloadAccordionState.kt` data class in `app/src/main/java/eu/kanade/tachiyomi/ui/download/anime/`
- [ ] T003 Create `AccordionType.kt` enum in `app/src/main/java/eu/kanade/tachiyomi/ui/download/manga/`
- [ ] T004 Create `AccordionType.kt` enum in `app/src/main/java/eu/kanade/tachiyomi/ui/download/anime/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T005 Create `AccordionHeaderItem.kt` in `app/src/main/java/eu/kanade/tachiyomi/ui/download/manga/`
- [ ] T006 Create `AccordionHeaderItem.kt` in `app/src/main/java/eu/kanade/tachiyomi/ui/download/anime/`
- [ ] T007 Create `AccordionHeaderHolder.kt` in `app/src/main/java/eu/kanade/tachiyomi/ui/download/manga/`
- [ ] T008 Create `AccordionHeaderHolder.kt` in `app/src/main/java/eu/kanade/tachiyomi/ui/download/anime/`
- [ ] T009 Create `download_accordion_header.xml` layout in `app/src/main/res/layout/`
- [ ] T010 [P] Add `accordionState` StateFlow to `MangaDownloadQueueScreenModel.kt`
- [ ] T011 [P] Add `accordionState` StateFlow to `AnimeDownloadQueueScreenModel.kt`
- [ ] T012 [P] Add `computeAccordionState()` method to `MangaDownloadQueueScreenModel.kt`
- [ ] T013 [P] Add `computeAccordionState()` method to `AnimeDownloadQueueScreenModel.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - View Active Download Item (Priority: P1) 🎯 MVP

**Goal**: Display the currently active downloading item always visible at the top of the interface

**Independent Test**: Open download queue screen and verify active item is displayed at top with correct progress indicator

### Tests for User Story 1

- [ ] T014 [P] [US1] Create unit test for `DownloadAccordionState` active item computation in `app/src/test/java/eu/kanade/tachiyomi/ui/download/manga/DownloadAccordionStateTest.kt`
- [ ] T015 [P] [US1] Create unit test for `DownloadAccordionState` active item computation in `app/src/test/java/eu/kanade/tachiyomi/ui/download/anime/DownloadAccordionStateTest.kt`

### Implementation for User Story 1

- [ ] T016 [P] [US1] Update `MangaDownloadQueueScreen.kt` to display active item separately from accordion
- [ ] T017 [P] [US1] Update `AnimeDownloadQueueScreen.kt` to display active item separately from accordion
- [ ] T018 [US1] Add `isDownloaderRunning` observation to both ScreenModels
- [ ] T019 [US1] Handle empty state when no active download exists

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Manage Pending Downloads (Priority: P2)

**Goal**: Display collapsible "Pending Items" list with count badge and "Clear All" action

**Independent Test**: Add items to download queue and verify pending accordion displays with correct count and clear functionality

### Tests for User Story 2

- [ ] T020 [P] [US2] Create unit test for pending items filtering in `MangaDownloadQueueScreenModel`
- [ ] T021 [P] [US2] Create unit test for pending items filtering in `AnimeDownloadQueueScreenModel`

### Implementation for User Story 2

- [ ] T022 [P] [US2] Update `MangaDownloadAdapter.kt` to support accordion header items
- [ ] T023 [P] [US2] Update `AnimeDownloadAdapter.kt` to support accordion header items
- [ ] T024 [US2] Add pending items to accordion state in `MangaDownloadQueueScreenModel.kt`
- [ ] T025 [US2] Add pending items to accordion state in `AnimeDownloadQueueScreenModel.kt`
- [ ] T026 [US2] Add `clearPending()` method to `MangaDownloadQueueScreenModel.kt`
- [ ] T027 [US2] Add `clearPending()` method to `AnimeDownloadQueueScreenModel.kt`
- [ ] T028 [US2] Add confirmation dialog state and handling in `MangaDownloadQueueScreenModel.kt`
- [ ] T029 [US2] Add confirmation dialog state and handling in `AnimeDownloadQueueScreenModel.kt`

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Review Completed Downloads (Priority: P3)

**Goal**: Display collapsible "Completed Items" list with history count badge and "Clear All" action

**Independent Test**: Complete downloads and verify they appear in completed accordion with correct count

### Tests for User Story 3

- [ ] T030 [P] [US3] Create unit test for completed items state transition in `MangaDownloadQueueScreenModel`
- [ ] T031 [P] [US3] Create unit test for completed items state transition in `AnimeDownloadQueueScreenModel`

### Implementation for User Story 3

- [ ] T032 [P] [US3] Add completed items to accordion state in `MangaDownloadQueueScreenModel.kt`
- [ ] T033 [P] [US3] Add completed items to accordion state in `AnimeDownloadQueueScreenModel.kt`
- [ ] T034 [US3] Add `clearCompleted()` method to `MangaDownloadQueueScreenModel.kt`
- [ ] T035 [US3] Add `clearCompleted()` method to `AnimeDownloadQueueScreenModel.kt`
- [ ] T036 [US3] Handle DOWNLOADED state transition in `onStatusChange()` for both ScreenModels

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: User Story 4 - Handle Failed Downloads (Priority: P4)

**Goal**: Display collapsible "Failed Items" list with error count badge and "Clear All" action

**Independent Test**: Simulate failed downloads and verify they appear in failed accordion

### Tests for User Story 4

- [ ] T037 [P] [US4] Create unit test for failed items state transition in `MangaDownloadQueueScreenModel`
- [ ] T038 [P] [US4] Create unit test for failed items state transition in `AnimeDownloadQueueScreenModel`

### Implementation for User Story 4

- [ ] T039 [P] [US4] Add failed items to accordion state in `MangaDownloadQueueScreenModel.kt`
- [ ] T040 [P] [US4] Add failed items to accordion state in `AnimeDownloadQueueScreenModel.kt`
- [ ] T041 [US4] Add `clearFailed()` method to `MangaDownloadQueueScreenModel.kt`
- [ ] T042 [US4] Add `clearFailed()` method to `AnimeDownloadQueueScreenModel.kt`
- [ ] T043 [US4] Handle ERROR state transition in `onStatusChange()` for both ScreenModels

---

## Phase 7: User Story 5 - Active Item State Transitions (Priority: P1)

**Goal**: Automatically transition completed/failed downloads from active slot to appropriate accordion

**Independent Test**: Allow a download to complete and verify it transitions to completed list

### Tests for User Story 5

- [ ] T044 [P] [US5] Create unit test for active-to-completed transition in `MangaDownloadQueueScreenModel`
- [ ] T045 [P] [US5] Create unit test for active-to-failed transition in `MangaDownloadQueueScreenModel`

### Implementation for User Story 5

- [ ] T046 [US5] Implement state transition logic in `MangaDownloadQueueScreenModel.onStatusChange()`
- [ ] T047 [US5] Implement state transition logic in `AnimeDownloadQueueScreenModel.onStatusChange()`
- [ ] T048 [US5] Add timestamp tracking for completed items

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T049 [P] Add string resources for accordion titles in `i18n-aniyomi/src/commonMain/moko-resources/`
- [ ] T050 [P] Add string resources for confirmation dialog in `i18n-aniyomi/src/commonMain/moko-resources/`
- [ ] T051 Code cleanup and refactoring for both manga and anime implementations
- [ ] T052 Performance optimization for large download queues
- [ ] T053 Run quickstart.md validation
- [ ] T054 Update documentation in `docs/`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3 → P4)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - May integrate with US1 but should be independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - May integrate with US1/US2 but should be independently testable
- **User Story 4 (P4)**: Can start after Foundational (Phase 2) - May integrate with US1/US2/US3 but should be independently testable
- **User Story 5 (P1)**: Can start after Foundational (Phase 2) - Core to US1 functionality

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Manga and anime implementations can be done in parallel by different developers

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task: "Create unit test for DownloadAccordionState active item computation in MangaDownloadQueueScreenModel"
Task: "Create unit test for DownloadAccordionState active item computation in AnimeDownloadQueueScreenModel"

# Launch all models for User Story 1 together:
Task: "Update MangaDownloadQueueScreen.kt to display active item separately"
Task: "Update AnimeDownloadQueueScreen.kt to display active item separately"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Add User Story 4 → Test independently → Deploy/Demo
6. Add User Story 5 → Test independently → Deploy/Demo
7. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: Manga implementation (all user stories)
   - Developer B: Anime implementation (all user stories)
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence