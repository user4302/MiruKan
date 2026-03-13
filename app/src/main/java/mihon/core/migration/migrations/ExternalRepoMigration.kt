package mihon.core.migration.migrations

import com.user4302.domain.source.service.SourcePreferences
import com.user4302.mika.core.common.preference.getAndSet
import mihon.core.migration.Migration
import mihon.core.migration.MigrationContext

class ExternalRepoMigration : Migration {
    override val version = 114f

    // Clean up external repos
    override suspend fun invoke(migrationContext: MigrationContext): Boolean {
        val sourcePreferences = migrationContext.get<SourcePreferences>() ?: return false

        sourcePreferences.mangaExtensionRepos().getAndSet { repos: Set<String> ->
            repos.map { repo: String -> "https://raw.githubusercontent.com/$repo/repo" }.toSet()
        }

        return true
    }
}
