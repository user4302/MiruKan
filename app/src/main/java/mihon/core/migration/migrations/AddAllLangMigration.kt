package mihon.core.migration.migrations

import com.user4302.domain.source.service.SourcePreferences
import com.user4302.mika.core.common.preference.plusAssign
import mihon.core.migration.Migration
import mihon.core.migration.MigrationContext

class AddAllLangMigration : Migration {
    override val version = 70f

    // Migration to add "all" to enabled langauges
    override suspend fun invoke(migrationContext: MigrationContext): Boolean {
        val sourcePreferences = migrationContext.get<SourcePreferences>() ?: return false

        if (sourcePreferences.enabledLanguages().isSet()) {
            sourcePreferences.enabledLanguages() += "all"
        }

        return true
    }
}
