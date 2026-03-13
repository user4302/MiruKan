package com.user4302.mika.data.backup.restore.restorers

import com.user4302.mika.data.backup.models.BackupExtensionRepos
import com.user4302.mika.data.handlers.manga.MangaDatabaseHandler
import mihon.domain.extensionrepo.manga.interactor.GetMangaExtensionRepo
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class MangaExtensionRepoRestorer(
    private val mangaHandler: MangaDatabaseHandler = Injekt.get(),
    private val getExtensionRepos: GetMangaExtensionRepo = Injekt.get(),
) {

    suspend operator fun invoke(
        backupRepo: BackupExtensionRepos,
    ) {
        val dbRepos = getExtensionRepos.getAll()
        val existingReposBySHA = dbRepos.associateBy { it.signingKeyFingerprint }
        val existingReposByUrl = dbRepos.associateBy { it.baseUrl }
        val urlExists = existingReposByUrl[backupRepo.baseUrl]
        val shaExists = existingReposBySHA[backupRepo.signingKeyFingerprint]
        if (urlExists != null && urlExists.signingKeyFingerprint != backupRepo.signingKeyFingerprint) {
            error("Already Exists with different signing key fingerprint")
        } else if (shaExists != null) {
            error("${shaExists.name} has the same signing key fingerprint")
        } else {
            mangaHandler.await {
                extension_reposQueries.insert(
                    backupRepo.baseUrl,
                    backupRepo.name,
                    backupRepo.shortName,
                    backupRepo.website,
                    backupRepo.signingKeyFingerprint,
                )
            }
        }
    }
}
