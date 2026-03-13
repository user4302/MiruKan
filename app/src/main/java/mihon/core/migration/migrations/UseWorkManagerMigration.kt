package mihon.core.migration.migrations

import android.app.Application
import com.user4302.mika.data.library.anime.AnimeLibraryUpdateJob
import com.user4302.mika.data.library.manga.MangaLibraryUpdateJob
import mihon.core.migration.Migration
import mihon.core.migration.MigrationContext

class UseWorkManagerMigration : Migration {
    override val version = 96f

    // Fully utilize WorkManager for library updates
    override suspend fun invoke(migrationContext: MigrationContext): Boolean {
        val context = migrationContext.get<Application>() ?: return false

        MangaLibraryUpdateJob.cancelAllWorks(context)
        AnimeLibraryUpdateJob.cancelAllWorks(context)
        MangaLibraryUpdateJob.setupTask(context)
        AnimeLibraryUpdateJob.setupTask(context)

        return true
    }
}
