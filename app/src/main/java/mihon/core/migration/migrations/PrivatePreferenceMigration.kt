package mihon.core.migration.migrations

import com.user4302.mika.core.common.preference.Preference
import com.user4302.mika.core.common.preference.PreferenceStore
import mihon.core.migration.Migration
import mihon.core.migration.MigrationContext

class PrivatePreferenceMigration : Migration {
    override val version = 116f

    override suspend fun invoke(migrationContext: MigrationContext): Boolean {
        val preferenceStore = migrationContext.get<PreferenceStore>() ?: return false

        replacePreferences(
            preferenceStore = preferenceStore,
            filterPredicate = { it.key.startsWith("pref_mangasync_") || it.key.startsWith("track_token_") },
            newKey = { Preference.privateKey(it) },
        )

        return true
    }
}
