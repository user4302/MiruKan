package com.user4302.mika.data.backup.create.creators

import com.user4302.mika.animesource.ConfigurableAnimeSource
import com.user4302.mika.animesource.preferenceKey
import com.user4302.mika.animesource.sourcePreferences
import com.user4302.mika.core.common.preference.Preference
import com.user4302.mika.core.common.preference.PreferenceStore
import com.user4302.mika.data.backup.models.BackupPreference
import com.user4302.mika.data.backup.models.BackupSourcePreferences
import com.user4302.mika.data.backup.models.BooleanPreferenceValue
import com.user4302.mika.data.backup.models.FloatPreferenceValue
import com.user4302.mika.data.backup.models.IntPreferenceValue
import com.user4302.mika.data.backup.models.LongPreferenceValue
import com.user4302.mika.data.backup.models.StringPreferenceValue
import com.user4302.mika.data.backup.models.StringSetPreferenceValue
import com.user4302.mika.domain.source.anime.service.AnimeSourceManager
import com.user4302.mika.domain.source.manga.service.MangaSourceManager
import com.user4302.mika.source.ConfigurableSource
import com.user4302.mika.source.preferenceKey
import com.user4302.mika.source.sourcePreferences
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class PreferenceBackupCreator(
    private val animeSourceManager: AnimeSourceManager = Injekt.get(),
    private val mangaSourceManager: MangaSourceManager = Injekt.get(),
    private val preferenceStore: PreferenceStore = Injekt.get(),
) {

    fun createApp(includePrivatePreferences: Boolean): List<BackupPreference> {
        return preferenceStore.getAll().toBackupPreferences()
            .withPrivatePreferences(includePrivatePreferences)
    }

    fun createSource(includePrivatePreferences: Boolean): List<BackupSourcePreferences> {
        val animePreferences = animeSourceManager.getCatalogueSources()
            .filterIsInstance<ConfigurableAnimeSource>()
            .map {
                BackupSourcePreferences(
                    it.preferenceKey(),
                    it.sourcePreferences().all.toBackupPreferences()
                        .withPrivatePreferences(includePrivatePreferences),
                )
            }
            .filter { it.prefs.isNotEmpty() }
        val mangaPreferences = mangaSourceManager.getCatalogueSources()
            .filterIsInstance<ConfigurableSource>()
            .map {
                BackupSourcePreferences(
                    it.preferenceKey(),
                    it.sourcePreferences().all.toBackupPreferences()
                        .withPrivatePreferences(includePrivatePreferences),
                )
            }
            .filter { it.prefs.isNotEmpty() }
        return animePreferences + mangaPreferences
    }

    @Suppress("UNCHECKED_CAST")
    private fun Map<String, *>.toBackupPreferences(): List<BackupPreference> {
        return this
            .filterKeys { !Preference.isAppState(it) }
            .mapNotNull { (key, value) ->
                when (value) {
                    is Int -> BackupPreference(key, IntPreferenceValue(value))
                    is Long -> BackupPreference(key, LongPreferenceValue(value))
                    is Float -> BackupPreference(key, FloatPreferenceValue(value))
                    is String -> BackupPreference(key, StringPreferenceValue(value))
                    is Boolean -> BackupPreference(key, BooleanPreferenceValue(value))
                    is Set<*> -> (value as? Set<String>)?.let {
                        BackupPreference(key, StringSetPreferenceValue(it))
                    }
                    else -> null
                }
            }
    }

    private fun List<BackupPreference>.withPrivatePreferences(include: Boolean) =
        if (include) {
            this
        } else {
            this.filter { !Preference.isPrivate(it.key) }
        }
}
