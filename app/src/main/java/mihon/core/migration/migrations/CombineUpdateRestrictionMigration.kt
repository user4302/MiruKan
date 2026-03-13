package mihon.core.migration.migrations

import android.app.Application
import androidx.preference.PreferenceManager
import com.user4302.mika.core.common.preference.minusAssign
import com.user4302.mika.domain.library.service.LibraryPreferences
import com.user4302.mika.domain.library.service.LibraryPreferences.Companion.ENTRY_NON_COMPLETED
import mihon.core.migration.Migration
import mihon.core.migration.MigrationContext

class CombineUpdateRestrictionMigration : Migration {
    override val version = 72f

    // Combine global update item restrictions
    override suspend fun invoke(migrationContext: MigrationContext): Boolean {
        val context = migrationContext.get<Application>() ?: return false
        val libraryPreferences = migrationContext.get<LibraryPreferences>() ?: return false
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val oldUpdateOngoingOnly = prefs.getBoolean(
            "pref_update_only_non_completed_key",
            true,
        )
        if (!oldUpdateOngoingOnly) {
            libraryPreferences.autoUpdateItemRestrictions() -= ENTRY_NON_COMPLETED
        }

        return true
    }
}
