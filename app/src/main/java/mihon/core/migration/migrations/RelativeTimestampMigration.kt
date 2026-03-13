package mihon.core.migration.migrations

import com.user4302.domain.ui.UiPreferences
import com.user4302.mika.core.common.preference.PreferenceStore
import mihon.core.migration.Migration
import mihon.core.migration.MigrationContext

class RelativeTimestampMigration : Migration {
    override val version = 106f

    // Bring back simplified relative timestamp setting
    override suspend fun invoke(migrationContext: MigrationContext): Boolean {
        val preferenceStore = migrationContext.get<PreferenceStore>() ?: return false
        val uiPreferences = migrationContext.get<UiPreferences>() ?: return false

        val pref = preferenceStore.getInt("relative_time", 7)
        if (pref.get() == 0) {
            uiPreferences.relativeTime().set(false)
        }

        return true
    }
}
