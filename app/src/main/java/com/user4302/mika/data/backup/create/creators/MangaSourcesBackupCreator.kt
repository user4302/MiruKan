package com.user4302.mika.data.backup.create.creators

import com.user4302.mika.data.backup.models.BackupManga
import com.user4302.mika.data.backup.models.BackupSource
import com.user4302.mika.domain.source.manga.service.MangaSourceManager
import com.user4302.mika.source.MangaSource
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class MangaSourcesBackupCreator(
    private val mangaSourceManager: MangaSourceManager = Injekt.get(),
) {

    operator fun invoke(mangas: List<BackupManga>): List<BackupSource> {
        return mangas
            .asSequence()
            .map(BackupManga::source)
            .distinct()
            .map(mangaSourceManager::getOrStub)
            .map { it.toBackupSource() }
            .toList()
    }
}

private fun MangaSource.toBackupSource() =
    BackupSource(
        name = this.name,
        sourceId = this.id,
    )
