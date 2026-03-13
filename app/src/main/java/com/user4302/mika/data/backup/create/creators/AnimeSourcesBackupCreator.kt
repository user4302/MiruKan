package com.user4302.mika.data.backup.create.creators

import com.user4302.mika.animesource.AnimeSource
import com.user4302.mika.data.backup.models.BackupAnime
import com.user4302.mika.data.backup.models.BackupAnimeSource
import com.user4302.mika.domain.source.anime.service.AnimeSourceManager
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class AnimeSourcesBackupCreator(
    private val animeSourceManager: AnimeSourceManager = Injekt.get(),
) {

    operator fun invoke(animes: List<BackupAnime>): List<BackupAnimeSource> {
        return animes
            .asSequence()
            .map(BackupAnime::source)
            .distinct()
            .map(animeSourceManager::getOrStub)
            .map { it.toBackupSource() }
            .toList()
    }
}

private fun AnimeSource.toBackupSource() =
    BackupAnimeSource(
        name = this.name,
        sourceId = this.id,
    )
