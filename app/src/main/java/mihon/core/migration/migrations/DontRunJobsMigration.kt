package mihon.core.migration.migrations

import com.user4302.mika.core.common.preference.getAndSet
import com.user4302.mika.domain.library.service.LibraryPreferences
import mihon.core.migration.Migration
import mihon.core.migration.MigrationContext

class DontRunJobsMigration : Migration {
    override val version = 105f

    // Don't run automatic backup or library update jobs if battery is low
    override suspend fun invoke(migrationContext: MigrationContext): Boolean {
        val libraryPreferences = migrationContext.get<LibraryPreferences>() ?: return false

        val pref = libraryPreferences.autoUpdateDeviceRestrictions()
        if (pref.isSet() && "battery_not_low" in pref.get()) {
            pref.getAndSet { it - "battery_not_low" }
        }

        return true
    }
}
