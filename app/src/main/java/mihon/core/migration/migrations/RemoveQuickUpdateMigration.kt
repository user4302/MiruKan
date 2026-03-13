package mihon.core.migration.migrations

import android.app.Application
import com.user4302.mika.data.library.anime.AnimeLibraryUpdateJob
import com.user4302.mika.data.library.manga.MangaLibraryUpdateJob
import com.user4302.mika.domain.library.service.LibraryPreferences
import mihon.core.migration.Migration
import mihon.core.migration.MigrationContext

class RemoveQuickUpdateMigration : Migration {
    override val version = 71f

    // Handle removed every 3, 4, 6, and 8 hour library updates
    override suspend fun invoke(migrationContext: MigrationContext): Boolean {
        val context = migrationContext.get<Application>() ?: return false
        val libraryPreferences = migrationContext.get<LibraryPreferences>() ?: return false

        val updateInterval = libraryPreferences.autoUpdateInterval().get()
        if (updateInterval in listOf(3, 4, 6, 8)) {
            libraryPreferences.autoUpdateInterval().set(12)
            MangaLibraryUpdateJob.setupTask(context, 12)
            AnimeLibraryUpdateJob.setupTask(context, 12)
        }

        return true
    }
}
