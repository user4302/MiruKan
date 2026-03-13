package com.user4302.presentation.more.settings.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.fastMap
import androidx.core.content.ContextCompat
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.user4302.mika.data.library.anime.AnimeLibraryUpdateJob
import com.user4302.mika.data.library.manga.MangaLibraryUpdateJob
import com.user4302.mika.domain.category.anime.interactor.GetAnimeCategories
import com.user4302.mika.domain.category.manga.interactor.GetMangaCategories
import com.user4302.mika.domain.category.manga.interactor.ResetMangaCategoryFlags
import com.user4302.mika.domain.category.model.Category
import com.user4302.mika.domain.library.service.LibraryPreferences
import com.user4302.mika.domain.library.service.LibraryPreferences.Companion.DEVICE_CHARGING
import com.user4302.mika.domain.library.service.LibraryPreferences.Companion.DEVICE_NETWORK_NOT_METERED
import com.user4302.mika.domain.library.service.LibraryPreferences.Companion.DEVICE_ONLY_ON_WIFI
import com.user4302.mika.domain.library.service.LibraryPreferences.Companion.ENTRY_HAS_UNVIEWED
import com.user4302.mika.domain.library.service.LibraryPreferences.Companion.ENTRY_NON_COMPLETED
import com.user4302.mika.domain.library.service.LibraryPreferences.Companion.ENTRY_NON_VIEWED
import com.user4302.mika.domain.library.service.LibraryPreferences.Companion.ENTRY_OUTSIDE_RELEASE_PERIOD
import com.user4302.mika.domain.library.service.LibraryPreferences.Companion.MARK_DUPLICATE_CHAPTER_READ_EXISTING
import com.user4302.mika.domain.library.service.LibraryPreferences.Companion.MARK_DUPLICATE_CHAPTER_READ_NEW
import com.user4302.mika.domain.library.service.LibraryPreferences.Companion.MARK_DUPLICATE_EPISODE_SEEN_EXISTING
import com.user4302.mika.domain.library.service.LibraryPreferences.Companion.MARK_DUPLICATE_EPISODE_SEEN_NEW
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.presentation.core.i18n.pluralStringResource
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.presentation.core.util.collectAsState
import com.user4302.mika.ui.category.CategoriesTab
import com.user4302.presentation.category.visualName
import com.user4302.presentation.more.settings.Preference
import com.user4302.presentation.more.settings.PreferenceItem
import com.user4302.presentation.more.settings.widget.TriStateListDialog
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

object SettingsLibraryScreen : SearchableSettings {

    @Composable
    @ReadOnlyComposable
    override fun getTitleRes() = AYMR.strings.pref_category_library

    @Composable
    override fun getPreferences(): List<Preference> {
        val getCategories = remember { Injekt.get<GetMangaCategories>() }
        val allCategories by getCategories.subscribe().collectAsState(initial = emptyList())
        val getAnimeCategories = remember { Injekt.get<GetAnimeCategories>() }
        val allAnimeCategories by getAnimeCategories.subscribe().collectAsState(initial = emptyList())
        val libraryPreferences = remember { Injekt.get<LibraryPreferences>() }

        return listOf(
            getCategoriesGroup(
                LocalNavigator.currentOrThrow,
                allCategories,
                allAnimeCategories,
                libraryPreferences,
            ),
            getGlobalUpdateGroup(allCategories, allAnimeCategories, libraryPreferences),
            getSeasonBehaviorGroup(libraryPreferences),
            getAnimeBehaviorGroup(libraryPreferences),
            getBehaviorGroup(libraryPreferences),
        )
    }

    @Composable
    private fun getCategoriesGroup(
        navigator: Navigator,
        allCategories: List<Category>,
        allAnimeCategories: List<Category>,
        libraryPreferences: LibraryPreferences,
    ): Preference.PreferenceGroup {
        val scope = rememberCoroutineScope()
        val userCategoriesCount = allCategories.filterNot(Category::isSystemCategory).size
        val userAnimeCategoriesCount = allAnimeCategories.filterNot(Category::isSystemCategory).size

        // For default category
        val mangaIds = listOf(libraryPreferences.defaultMangaCategory().defaultValue()) +
            allCategories.fastMap { it.id.toInt() }
        val animeIds = listOf(libraryPreferences.defaultAnimeCategory().defaultValue()) +
            allAnimeCategories.fastMap { it.id.toInt() }

        val mangaLabels = listOf(stringResource(AYMR.strings.default_category_summary)) +
            allCategories.fastMap { it.visualName }
        val animeLabels = listOf(stringResource(AYMR.strings.default_category_summary)) +
            allAnimeCategories.fastMap { it.visualName }

        return Preference.PreferenceGroup(
            title = stringResource(AYAYMR.strings.general_categories),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(AYAYMR.strings.action_edit_anime_categories),
                    subtitle = pluralStringResource(
                        AYMR.plurals.num_categories,
                        count = userAnimeCategoriesCount,
                        userAnimeCategoriesCount,
                    ),
                    onClick = { navigator.push(CategoriesTab) },
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = libraryPreferences.defaultAnimeCategory(),
                    entries = animeIds.zip(animeLabels).toMap().toImmutableMap(),
                    title = stringResource(AYAYMR.strings.default_anime_category),
                ),
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(AYAYMR.strings.action_edit_manga_categories),
                    subtitle = pluralStringResource(
                        AYMR.plurals.num_categories,
                        count = userCategoriesCount,
                        userCategoriesCount,
                    ),
                    onClick = {
                        navigator.push(CategoriesTab)
                        CategoriesTab.showMangaCategory()
                    },
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = libraryPreferences.defaultMangaCategory(),
                    entries = mangaIds.zip(mangaLabels).toMap().toImmutableMap(),
                    title = stringResource(AYAYMR.strings.default_manga_category),
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = libraryPreferences.categorizedDisplaySettings(),
                    title = stringResource(AYMR.strings.categorized_display_settings),
                    onValueChanged = {
                        if (!it) {
                            scope.launch {
                                Injekt.get<ResetMangaCategoryFlags>().await()
                            }
                        }
                        true
                    },
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = libraryPreferences.hideHiddenCategoriesSettings(),
                    title = stringResource(AYAYMR.strings.pref_category_hide_hidden),
                ),
            ),
        )
    }

    @Composable
    private fun getGlobalUpdateGroup(
        allMangaCategories: List<Category>,
        allAnimeCategories: List<Category>,
        libraryPreferences: LibraryPreferences,
    ): Preference.PreferenceGroup {
        val context = LocalContext.current

        val autoUpdateIntervalPref = libraryPreferences.autoUpdateInterval()
        val autoUpdateInterval by autoUpdateIntervalPref.collectAsState()

        val animeAutoUpdateCategoriesPref = libraryPreferences.animeUpdateCategories()
        val animeAutoUpdateCategoriesExcludePref =
            libraryPreferences.animeUpdateCategoriesExclude()

        val includedAnime by animeAutoUpdateCategoriesPref.collectAsState()
        val excludedAnime by animeAutoUpdateCategoriesExcludePref.collectAsState()
        var showAnimeCategoriesDialog by rememberSaveable { mutableStateOf(false) }
        if (showAnimeCategoriesDialog) {
            TriStateListDialog(
                title = stringResource(AYAYMR.strings.anime_categories),
                message = stringResource(AYAYMR.strings.pref_anime_library_update_categories_details),
                items = allAnimeCategories,
                initialChecked = includedAnime.mapNotNull { id -> allAnimeCategories.find { it.id.toString() == id } },
                initialInversed = excludedAnime.mapNotNull { id -> allAnimeCategories.find { it.id.toString() == id } },
                itemLabel = { it.visualName },
                onDismissRequest = { showAnimeCategoriesDialog = false },
                onValueChanged = { newIncluded, newExcluded ->
                    animeAutoUpdateCategoriesPref.set(newIncluded.map { it.id.toString() }.toSet())
                    animeAutoUpdateCategoriesExcludePref.set(
                        newExcluded.map { it.id.toString() }
                            .toSet(),
                    )
                    showAnimeCategoriesDialog = false
                },
            )
        }

        val autoUpdateCategoriesPref = libraryPreferences.mangaUpdateCategories()
        val autoUpdateCategoriesExcludePref =
            libraryPreferences.mangaUpdateCategoriesExclude()

        val includedManga by autoUpdateCategoriesPref.collectAsState()
        val excludedManga by autoUpdateCategoriesExcludePref.collectAsState()
        var showMangaCategoriesDialog by rememberSaveable { mutableStateOf(false) }
        if (showMangaCategoriesDialog) {
            TriStateListDialog(
                title = stringResource(AYAYMR.strings.manga_categories),
                message = stringResource(AYAYMR.strings.pref_manga_library_update_categories_details),
                items = allMangaCategories,
                initialChecked = includedManga.mapNotNull { id -> allMangaCategories.find { it.id.toString() == id } },
                initialInversed = excludedManga.mapNotNull { id -> allMangaCategories.find { it.id.toString() == id } },
                itemLabel = { it.visualName },
                onDismissRequest = { showMangaCategoriesDialog = false },
                onValueChanged = { newIncluded, newExcluded ->
                    autoUpdateCategoriesPref.set(newIncluded.map { it.id.toString() }.toSet())
                    autoUpdateCategoriesExcludePref.set(
                        newExcluded.map { it.id.toString() }
                            .toSet(),
                    )
                    showMangaCategoriesDialog = false
                },
            )
        }

        return Preference.PreferenceGroup(
            title = stringResource(AYMR.strings.pref_category_library_update),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.ListPreference(
                    preference = autoUpdateIntervalPref,
                    entries = persistentMapOf(
                        0 to stringResource(AYMR.strings.update_never),
                        12 to stringResource(AYMR.strings.update_12hour),
                        24 to stringResource(AYMR.strings.update_24hour),
                        48 to stringResource(AYMR.strings.update_48hour),
                        72 to stringResource(AYMR.strings.update_72hour),
                        168 to stringResource(AYMR.strings.update_weekly),
                    ),
                    title = stringResource(AYMR.strings.pref_library_update_interval),
                    onValueChanged = {
                        MangaLibraryUpdateJob.setupTask(context, it)
                        AnimeLibraryUpdateJob.setupTask(context, it)
                        true
                    },
                ),
                Preference.PreferenceItem.MultiSelectListPreference(
                    preference = libraryPreferences.autoUpdateDeviceRestrictions(),
                    entries = persistentMapOf(
                        DEVICE_ONLY_ON_WIFI to stringResource(AYMR.strings.connected_to_wifi),
                        DEVICE_NETWORK_NOT_METERED to stringResource(AYMR.strings.network_not_metered),
                        DEVICE_CHARGING to stringResource(AYMR.strings.charging),
                    ),
                    title = stringResource(AYMR.strings.pref_library_update_restriction),
                    subtitle = stringResource(AYMR.strings.restrictions),
                    enabled = autoUpdateInterval > 0,
                    onValueChanged = {
                        // Post to event looper to allow the preference to be updated.
                        ContextCompat.getMainExecutor(context).execute {
                            MangaLibraryUpdateJob.setupTask(context)
                            AnimeLibraryUpdateJob.setupTask(context)
                        }
                        true
                    },
                ),
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(AYAYMR.strings.anime_categories),
                    subtitle = getCategoriesLabel(
                        allCategories = allAnimeCategories,
                        included = includedAnime,
                        excluded = excludedAnime,
                    ),
                    onClick = { showAnimeCategoriesDialog = true },
                ),
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(AYAYMR.strings.manga_categories),
                    subtitle = getCategoriesLabel(
                        allCategories = allMangaCategories,
                        included = includedManga,
                        excluded = excludedManga,
                    ),
                    onClick = { showMangaCategoriesDialog = true },
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = libraryPreferences.autoUpdateMetadata(),
                    title = stringResource(AYMR.strings.pref_library_update_refresh_metadata),
                    subtitle = stringResource(AYMR.strings.pref_library_update_refresh_metadata_summary),
                ),
                Preference.PreferenceItem.MultiSelectListPreference(
                    preference = libraryPreferences.autoUpdateItemRestrictions(),
                    entries = persistentMapOf(
                        ENTRY_HAS_UNVIEWED to stringResource(AYAYMR.strings.pref_update_only_completely_read),
                        ENTRY_NON_VIEWED to stringResource(AYMR.strings.pref_update_only_started),
                        ENTRY_NON_COMPLETED to stringResource(AYMR.strings.pref_update_only_non_completed),
                        ENTRY_OUTSIDE_RELEASE_PERIOD to stringResource(AYMR.strings.pref_update_only_in_release_period),
                    ),
                    title = stringResource(AYMR.strings.pref_library_update_smart_update),
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = libraryPreferences.newShowUpdatesCount(),
                    title = stringResource(AYAYMR.strings.pref_library_update_show_tab_badge),
                ),
            ),
        )
    }

    @Composable
    private fun getSeasonBehaviorGroup(
        libraryPreferences: LibraryPreferences,
    ): Preference.PreferenceGroup {
        return Preference.PreferenceGroup(
            title = stringResource(AYAYMR.strings.pref_library_season),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.SwitchPreference(
                    preference = libraryPreferences.updateSeasonOnRefresh(),
                    title = stringResource(AYAYMR.strings.pref_update_seasons_refresh),
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = libraryPreferences.updateSeasonOnLibraryUpdate(),
                    title = stringResource(AYAYMR.strings.pref_update_seasons_update),
                ),
            ),
        )
    }

    @Composable
    private fun getBehaviorGroup(
        libraryPreferences: LibraryPreferences,
    ): Preference.PreferenceGroup {
        return Preference.PreferenceGroup(
            title = stringResource(AYAYMR.strings.pref_behavior),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.ListPreference(
                    preference = libraryPreferences.swipeChapterStartAction(),
                    entries = persistentMapOf(
                        LibraryPreferences.ChapterSwipeAction.Disabled to
                            stringResource(AYMR.strings.disabled),
                        LibraryPreferences.ChapterSwipeAction.ToggleBookmark to
                            stringResource(AYMR.strings.action_bookmark),
                        LibraryPreferences.ChapterSwipeAction.ToggleRead to
                            stringResource(AYMR.strings.action_mark_as_read),
                        LibraryPreferences.ChapterSwipeAction.Download to
                            stringResource(AYMR.strings.action_download),
                    ),
                    title = stringResource(AYMR.strings.pref_chapter_swipe_start),
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = libraryPreferences.swipeChapterEndAction(),
                    entries = persistentMapOf(
                        LibraryPreferences.ChapterSwipeAction.Disabled to
                            stringResource(AYMR.strings.disabled),
                        LibraryPreferences.ChapterSwipeAction.ToggleBookmark to
                            stringResource(AYMR.strings.action_bookmark),
                        LibraryPreferences.ChapterSwipeAction.ToggleRead to
                            stringResource(AYMR.strings.action_mark_as_read),
                        LibraryPreferences.ChapterSwipeAction.Download to
                            stringResource(AYMR.strings.action_download),
                    ),
                    title = stringResource(AYMR.strings.pref_chapter_swipe_end),
                ),
                Preference.PreferenceItem.MultiSelectListPreference(
                    preference = libraryPreferences.markDuplicateReadChapterAsRead(),
                    entries = persistentMapOf(
                        MARK_DUPLICATE_CHAPTER_READ_EXISTING to
                            stringResource(AYMR.strings.pref_mark_duplicate_read_chapter_read_existing),
                        MARK_DUPLICATE_CHAPTER_READ_NEW to
                            stringResource(AYMR.strings.pref_mark_duplicate_read_chapter_read_new),
                    ),
                    title = stringResource(AYMR.strings.pref_mark_duplicate_read_chapter_read),
                ),
            ),
        )
    }

    @Composable
    private fun getAnimeBehaviorGroup(
        libraryPreferences: LibraryPreferences,
    ): Preference.PreferenceGroup {
        return Preference.PreferenceGroup(
            title = stringResource(AYAYMR.strings.pref_behavior_episode),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.ListPreference(
                    preference = libraryPreferences.swipeEpisodeStartAction(),
                    entries = persistentMapOf(
                        LibraryPreferences.EpisodeSwipeAction.Disabled to
                            stringResource(AYMR.strings.disabled),
                        LibraryPreferences.EpisodeSwipeAction.ToggleBookmark to
                            stringResource(AYAYMR.strings.action_bookmark_episode),
                        LibraryPreferences.EpisodeSwipeAction.ToggleFillermark to
                            stringResource(AYAYMR.strings.action_fillermark_episode),
                        LibraryPreferences.EpisodeSwipeAction.ToggleSeen to
                            stringResource(AYAYMR.strings.action_mark_as_seen),
                        LibraryPreferences.EpisodeSwipeAction.Download to
                            stringResource(AYMR.strings.action_download),
                    ),
                    title = stringResource(AYAYMR.strings.pref_episode_swipe_start),
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = libraryPreferences.swipeEpisodeEndAction(),
                    entries = persistentMapOf(
                        LibraryPreferences.EpisodeSwipeAction.Disabled to
                            stringResource(AYMR.strings.disabled),
                        LibraryPreferences.EpisodeSwipeAction.ToggleBookmark to
                            stringResource(AYAYMR.strings.action_bookmark_episode),
                        LibraryPreferences.EpisodeSwipeAction.ToggleFillermark to
                            stringResource(AYAYMR.strings.action_fillermark_episode),
                        LibraryPreferences.EpisodeSwipeAction.ToggleSeen to
                            stringResource(AYAYMR.strings.action_mark_as_seen),
                        LibraryPreferences.EpisodeSwipeAction.Download to
                            stringResource(AYMR.strings.action_download),
                    ),
                    title = stringResource(AYAYMR.strings.pref_episode_swipe_end),
                ),
                Preference.PreferenceItem.MultiSelectListPreference(
                    preference = libraryPreferences.markDuplicateSeenEpisodeAsSeen(),
                    entries = persistentMapOf(
                        MARK_DUPLICATE_EPISODE_SEEN_EXISTING to
                            stringResource(AYAYMR.strings.pref_mark_duplicate_seen_episode_seen_existing),
                        MARK_DUPLICATE_EPISODE_SEEN_NEW to
                            stringResource(AYAYMR.strings.pref_mark_duplicate_seen_episode_seen_new),
                    ),
                    title = stringResource(AYAYMR.strings.pref_mark_duplicate_seen_episode_seen),
                ),
            ),
        )
    }
}
