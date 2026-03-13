package com.user4302.mika.ui.browse.manga.migration.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.StateScreenModel
import com.user4302.domain.entries.manga.interactor.UpdateManga
import com.user4302.domain.entries.manga.model.hasCustomCover
import com.user4302.domain.entries.manga.model.toSManga
import com.user4302.domain.items.chapter.interactor.SyncChaptersWithSource
import com.user4302.mika.core.common.preference.Preference
import com.user4302.mika.core.common.preference.PreferenceStore
import com.user4302.mika.core.common.util.lang.launchIO
import com.user4302.mika.core.common.util.lang.withUIContext
import com.user4302.mika.data.cache.MangaCoverCache
import com.user4302.mika.data.download.manga.MangaDownloadManager
import com.user4302.mika.data.track.EnhancedMangaTracker
import com.user4302.mika.data.track.TrackerManager
import com.user4302.mika.domain.category.manga.interactor.GetMangaCategories
import com.user4302.mika.domain.category.manga.interactor.SetMangaCategories
import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.domain.entries.manga.model.MangaUpdate
import com.user4302.mika.domain.items.chapter.interactor.GetChaptersByMangaId
import com.user4302.mika.domain.items.chapter.interactor.UpdateChapter
import com.user4302.mika.domain.items.chapter.model.toChapterUpdate
import com.user4302.mika.domain.source.manga.service.MangaSourceManager
import com.user4302.mika.domain.track.manga.interactor.GetMangaTracks
import com.user4302.mika.domain.track.manga.interactor.InsertMangaTrack
import com.user4302.mika.i18n.MR
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.presentation.core.components.LabeledCheckbox
import com.user4302.mika.presentation.core.components.material.padding
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.presentation.core.screens.LoadingScreen
import com.user4302.mika.source.MangaSource
import com.user4302.mika.source.model.SChapter
import com.user4302.mika.ui.browse.manga.migration.MangaMigrationFlags
import kotlinx.coroutines.flow.update
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.time.Instant

@Composable
internal fun MigrateMangaDialog(
    oldManga: Manga,
    newManga: Manga,
    screenModel: MigrateMangaDialogScreenModel,
    onDismissRequest: () -> Unit,
    onClickTitle: () -> Unit,
    onPopScreen: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val state by screenModel.state.collectAsState()

    val flags = remember { MangaMigrationFlags.getFlags(oldManga, screenModel.migrateFlags.get()) }
    val selectedFlags = remember { flags.map { it.isDefaultSelected }.toMutableStateList() }

    if (state.isMigrating) {
        LoadingScreen(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f)),
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(MR.strings.migration_dialog_what_to_include))
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                ) {
                    flags.forEachIndexed { index, flag ->
                        LabeledCheckbox(
                            label = stringResource(flag.titleId),
                            checked = selectedFlags[index],
                            onCheckedChange = { selectedFlags[index] = it },
                        )
                    }
                }
            },
            confirmButton = {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
                ) {
                    TextButton(
                        onClick = {
                            onDismissRequest()
                            onClickTitle()
                        },
                    ) {
                        Text(text = stringResource(AYMR.strings.action_show_manga))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(
                        onClick = {
                            scope.launchIO {
                                screenModel.migrateManga(
                                    oldManga,
                                    newManga,
                                    false,
                                    MangaMigrationFlags.getSelectedFlagsBitMap(selectedFlags, flags),
                                )
                                withUIContext { onPopScreen() }
                            }
                        },
                    ) {
                        Text(text = stringResource(MR.strings.copy))
                    }
                    TextButton(
                        onClick = {
                            scope.launchIO {
                                screenModel.migrateManga(
                                    oldManga,
                                    newManga,
                                    true,
                                    MangaMigrationFlags.getSelectedFlagsBitMap(selectedFlags, flags),
                                )

                                withUIContext { onPopScreen() }
                            }
                        },
                    ) {
                        Text(text = stringResource(MR.strings.migrate))
                    }
                }
            },
        )
    }
}

internal class MigrateMangaDialogScreenModel(
    private val sourceManager: MangaSourceManager = Injekt.get(),
    private val downloadManager: MangaDownloadManager = Injekt.get(),
    private val updateManga: UpdateManga = Injekt.get(),
    private val getChaptersByMangaId: GetChaptersByMangaId = Injekt.get(),
    private val syncChaptersWithSource: SyncChaptersWithSource = Injekt.get(),
    private val updateChapter: UpdateChapter = Injekt.get(),
    private val getCategories: GetMangaCategories = Injekt.get(),
    private val setMangaCategories: SetMangaCategories = Injekt.get(),
    private val getTracks: GetMangaTracks = Injekt.get(),
    private val insertTrack: InsertMangaTrack = Injekt.get(),
    private val coverCache: MangaCoverCache = Injekt.get(),
    private val preferenceStore: PreferenceStore = Injekt.get(),
) : StateScreenModel<MigrateMangaDialogScreenModel.State>(State()) {

    val migrateFlags: Preference<Int> by lazy {
        preferenceStore.getInt("migrate_flags", Int.MAX_VALUE)
    }

    private val enhancedServices by lazy {
        Injekt.get<TrackerManager>().trackers.filterIsInstance<EnhancedMangaTracker>()
    }

    suspend fun migrateManga(
        oldManga: Manga,
        newManga: Manga,
        replace: Boolean,
        flags: Int,
    ) {
        migrateFlags.set(flags)
        val source = sourceManager.get(newManga.source) ?: return
        val prevSource = sourceManager.get(oldManga.source)

        mutableState.update { it.copy(isMigrating = true) }

        try {
            val chapters = source.getChapterList(newManga.toSManga())

            migrateMangaInternal(
                oldSource = prevSource,
                newSource = source,
                oldManga = oldManga,
                newManga = newManga,
                sourceChapters = chapters,
                replace = replace,
                flags = flags,
            )
        } catch (_: Throwable) {
            // Explicitly stop if an error occurred; the dialog normally gets popped at the end
            // anyway
            mutableState.update { it.copy(isMigrating = false) }
        }
    }

    private suspend fun migrateMangaInternal(
        oldSource: MangaSource?,
        newSource: MangaSource,
        oldManga: Manga,
        newManga: Manga,
        sourceChapters: List<SChapter>,
        replace: Boolean,
        flags: Int,
    ) {
        val migrateChapters = MangaMigrationFlags.hasChapters(flags)
        val migrateCategories = MangaMigrationFlags.hasCategories(flags)
        val migrateCustomCover = MangaMigrationFlags.hasCustomCover(flags)
        val deleteDownloaded = MangaMigrationFlags.hasDeleteDownloaded(flags)

        try {
            syncChaptersWithSource.await(sourceChapters, newManga, newSource)
        } catch (_: Exception) {
            // Worst case, chapters won't be synced
        }

        // Update chapters read, bookmark and dateFetch
        if (migrateChapters) {
            val prevMangaChapters = getChaptersByMangaId.await(oldManga.id)
            val mangaChapters = getChaptersByMangaId.await(newManga.id)

            val maxChapterRead = prevMangaChapters
                .filter { it.read }
                .maxOfOrNull { it.chapterNumber }

            val updatedMangaChapters = mangaChapters.map { mangaChapter ->
                var updatedChapter = mangaChapter
                if (updatedChapter.isRecognizedNumber) {
                    val prevChapter = prevMangaChapters
                        .find { it.isRecognizedNumber && it.chapterNumber == updatedChapter.chapterNumber }

                    if (prevChapter != null) {
                        updatedChapter = updatedChapter.copy(
                            dateFetch = prevChapter.dateFetch,
                            bookmark = prevChapter.bookmark,
                        )
                    }

                    if (maxChapterRead != null && updatedChapter.chapterNumber <= maxChapterRead) {
                        updatedChapter = updatedChapter.copy(read = true)
                    }
                }

                updatedChapter
            }

            val chapterUpdates = updatedMangaChapters.map { it.toChapterUpdate() }
            updateChapter.awaitAll(chapterUpdates)
        }

        // Update categories
        if (migrateCategories) {
            val categoryIds = getCategories.await(oldManga.id).map { it.id }
            setMangaCategories.await(newManga.id, categoryIds)
        }

        // Update track
        getTracks.await(oldManga.id).mapNotNull { track ->
            val updatedTrack = track.copy(mangaId = newManga.id)

            val service = enhancedServices
                .firstOrNull { it.isTrackFrom(updatedTrack, oldManga, oldSource) }

            if (service != null) {
                service.migrateTrack(updatedTrack, newManga, newSource)
            } else {
                updatedTrack
            }
        }
            .takeIf { it.isNotEmpty() }
            ?.let { insertTrack.awaitAll(it) }

        // Delete downloaded
        if (deleteDownloaded) {
            if (oldSource != null) {
                downloadManager.deleteManga(oldManga, oldSource)
            }
        }

        if (replace) {
            updateManga.awaitUpdateFavorite(oldManga.id, favorite = false)
        }

        // Update custom cover (recheck if custom cover exists)
        if (migrateCustomCover && oldManga.hasCustomCover()) {
            coverCache.setCustomCoverToCache(
                newManga,
                coverCache.getCustomCoverFile(oldManga.id).inputStream(),
            )
        }

        updateManga.await(
            MangaUpdate(
                id = newManga.id,
                favorite = true,
                chapterFlags = oldManga.chapterFlags,
                viewerFlags = oldManga.viewerFlags,
                dateAdded = if (replace) oldManga.dateAdded else Instant.now().toEpochMilli(),
            ),
        )
    }

    @Immutable
    data class State(
        val isMigrating: Boolean = false,
    )
}
