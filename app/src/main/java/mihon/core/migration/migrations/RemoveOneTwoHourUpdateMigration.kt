package mihon.core.migration.migrations

import android.app.Application
import com.user4302.mika.data.library.anime.AnimeLibraryUpdateJob
import com.user4302.mika.data.library.manga.MangaLibraryUpdateJob
import com.user4302.mika.domain.library.service.LibraryPreferences
import mihon.core.migration.Migration
import mihon.core.migration.MigrationContext

class RemoveOneTwoHourUpdateMigration : Migration {
    override val version = 61f

    // Handle removed every 1 or 2 hour library updates
    override suspend fun invoke(migrationContext: MigrationContext): Boolean {
        val context = migrationContext.get<Application>() ?: return false
        val libraryPreferences = migrationContext.get<LibraryPreferences>() ?: return false

        val updateInterval = libraryPreferences.autoUpdateInterval().get()
        if (updateInterval == 1 || updateInterval == 2) {
            libraryPreferences.autoUpdateInterval().set(3)
            MangaLibraryUpdateJob.setupTask(context, 3)
            AnimeLibraryUpdateJob.setupTask(context, 3)
        }

        return true
    }
}
