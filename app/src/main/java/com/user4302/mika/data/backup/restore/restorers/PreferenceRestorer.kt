package com.user4302.mika.data.backup.restore.restorers

import android.content.Context
import android.util.Log
import com.user4302.mika.core.common.preference.AndroidPreferenceStore
import com.user4302.mika.core.common.preference.PreferenceStore
import com.user4302.mika.core.common.preference.plusAssign
import com.user4302.mika.data.backup.create.BackupCreateJob
import com.user4302.mika.data.backup.models.BackupCategory
import com.user4302.mika.data.backup.models.BackupPreference
import com.user4302.mika.data.backup.models.BackupSourcePreferences
import com.user4302.mika.data.backup.models.BooleanPreferenceValue
import com.user4302.mika.data.backup.models.FloatPreferenceValue
import com.user4302.mika.data.backup.models.IntPreferenceValue
import com.user4302.mika.data.backup.models.LongPreferenceValue
import com.user4302.mika.data.backup.models.StringPreferenceValue
import com.user4302.mika.data.backup.models.StringSetPreferenceValue
import com.user4302.mika.data.library.anime.AnimeLibraryUpdateJob
import com.user4302.mika.data.library.manga.MangaLibraryUpdateJob
import com.user4302.mika.domain.category.anime.interactor.GetAnimeCategories
import com.user4302.mika.domain.category.manga.interactor.GetMangaCategories
import com.user4302.mika.domain.category.model.Category
import com.user4302.mika.domain.download.service.DownloadPreferences
import com.user4302.mika.domain.library.service.LibraryPreferences
import com.user4302.mika.source.sourcePreferences
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class PreferenceRestorer(
    private val context: Context,
    private val getMangaCategories: GetMangaCategories = Injekt.get(),
    private val getAnimeCategories: GetAnimeCategories = Injekt.get(),
    private val preferenceStore: PreferenceStore = Injekt.get(),
) {
    suspend fun restoreApp(
        preferences: List<BackupPreference>,
        backupCategories: List<BackupCategory>?,
    ) {
        restorePreferences(
            preferences,
            preferenceStore,
            backupCategories,
        )

        AnimeLibraryUpdateJob.setupTask(context)
        MangaLibraryUpdateJob.setupTask(context)
        BackupCreateJob.setupTask(context)
    }

    suspend fun restoreSource(preferences: List<BackupSourcePreferences>) {
        preferences.forEach {
            val sourcePrefs = AndroidPreferenceStore(context, sourcePreferences(it.sourceKey))
            restorePreferences(it.prefs, sourcePrefs)
        }
    }

    private suspend fun restorePreferences(
        toRestore: List<BackupPreference>,
        preferenceStore: PreferenceStore,
        backupCategories: List<BackupCategory>? = null,
    ) {
        val allMangaCategories = if (backupCategories != null) getMangaCategories.await() else emptyList()
        val allAnimeCategories = if (backupCategories != null) getAnimeCategories.await() else emptyList()

        val mangaCategoriesByName = allMangaCategories.associateBy { it.name }
        val animeCategoriesByName = allAnimeCategories.associateBy { it.name }
        val backupCategoriesById = backupCategories?.associateBy { it.id.toString() }.orEmpty()

        val prefs = preferenceStore.getAll()
        toRestore.forEach { (key, value) ->
            try {
                when (value) {
                    is IntPreferenceValue -> {
                        if (prefs[key] is Int?) {
                            val newValue = if (key == LibraryPreferences.DEFAULT_MANGA_CATEGORY_PREF_KEY) {
                                backupCategoriesById[value.value.toString()]
                                    ?.let { mangaCategoriesByName[it.name]?.id?.toInt() }
                            } else if (key == LibraryPreferences.DEFAULT_ANIME_CATEGORY_PREF_KEY) {
                                backupCategoriesById[value.value.toString()]
                                    ?.let { animeCategoriesByName[it.name]?.id?.toInt() }
                            } else {
                                value.value
                            }

                            newValue?.let { preferenceStore.getInt(key).set(it) }
                        }
                    }
                    is LongPreferenceValue -> {
                        if (prefs[key] is Long?) {
                            preferenceStore.getLong(key).set(value.value)
                        }
                    }
                    is FloatPreferenceValue -> {
                        if (prefs[key] is Float?) {
                            preferenceStore.getFloat(key).set(value.value)
                        }
                    }
                    is StringPreferenceValue -> {
                        if (prefs[key] is String?) {
                            preferenceStore.getString(key).set(value.value)
                        }
                    }
                    is BooleanPreferenceValue -> {
                        if (prefs[key] is Boolean?) {
                            preferenceStore.getBoolean(key).set(value.value)
                        }
                    }
                    is StringSetPreferenceValue -> {
                        if (prefs[key] is Set<*>?) {
                            val restored = restoreCategoriesPreference(
                                key,
                                value.value,
                                preferenceStore,
                                backupCategoriesById,
                                mangaCategoriesByName,
                                animeCategoriesByName,
                            )
                            if (!restored) preferenceStore.getStringSet(key).set(value.value)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("PreferenceRestorer", "Failed to restore preference <$key>", e)
            }
        }
    }

    private fun restoreCategoriesPreference(
        key: String,
        value: Set<String>,
        preferenceStore: PreferenceStore,
        backupCategoriesById: Map<String, BackupCategory>,
        mangaCategoriesByName: Map<String, Category>,
        animeCategoriesByName: Map<String, Category>,
    ): Boolean {
        val categoryPreferences = LibraryPreferences.categoryPreferenceKeys + DownloadPreferences.categoryPreferenceKeys
        if (key !in categoryPreferences) return false

        val ids = value.flatMap {
            listOf(
                backupCategoriesById[it]?.name?.let { name ->
                    mangaCategoriesByName[name]?.id?.toString()
                },
                backupCategoriesById[it]?.name?.let { name ->
                    animeCategoriesByName[name]?.id?.toString()
                },
            )
        }.filterNotNull()

        if (ids.isNotEmpty()) {
            preferenceStore.getStringSet(key) += ids
        }
        return true
    }
}
