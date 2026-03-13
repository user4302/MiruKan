package mihon.core.migration.migrations

import android.app.Application
import com.user4302.mika.data.backup.create.BackupCreateJob
import com.user4302.mika.domain.backup.service.BackupPreferences
import mihon.core.migration.Migration
import mihon.core.migration.MigrationContext

class EnableAutoBackupMigration : Migration {
    override val version = 84f

    // Always attempt automatic backup creation
    override suspend fun invoke(migrationContext: MigrationContext): Boolean {
        val context = migrationContext.get<Application>() ?: return false
        val backupPreferences = migrationContext.get<BackupPreferences>() ?: return false

        if (backupPreferences.backupInterval().get() == 0) {
            backupPreferences.backupInterval().set(12)
            BackupCreateJob.setupTask(context)
        }

        return true
    }
}
