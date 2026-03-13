package mihon.core.migration.migrations

import com.user4302.domain.source.service.SourcePreferences
import com.user4302.mika.core.common.util.system.logcat
import logcat.LogPriority
import mihon.core.migration.Migration
import mihon.core.migration.MigrationContext
import mihon.domain.extensionrepo.anime.repository.AnimeExtensionRepoRepository
import mihon.domain.extensionrepo.manga.repository.MangaExtensionRepoRepository

class TrustExtensionRepositoryMigration : Migration {
    override val version: Float = 34f

    override suspend fun invoke(migrationContext: MigrationContext): Boolean {
        val mangaRepoRepo: MangaExtensionRepoRepository? = migrationContext.get()
        val animeRepoRepo: AnimeExtensionRepoRepository? = migrationContext.get()
        val preferences: SourcePreferences? = migrationContext.get()

        try {
            // This migration is no longer needed as trusted repos are handled differently
            logcat(LogPriority.INFO) { "TrustExtensionRepositoryMigration skipped - not applicable" }
            return true
        } catch (e: Exception) {
            logcat(LogPriority.ERROR) { "Failed to trust extension repos: ${e.message}" }
            return false
        }
    }
}
