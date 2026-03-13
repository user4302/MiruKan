package com.user4302.mika.domain.entries.anime.interactor

import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.entries.anime.repository.AnimeRepository
import com.user4302.mika.domain.library.anime.LibraryAnime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retry
import logcat.LogPriority
import kotlin.time.Duration.Companion.seconds

class GetLibraryAnime(
    private val animeRepository: AnimeRepository,
) {

    suspend fun await(): List<LibraryAnime> {
        return animeRepository.getLibraryAnime()
    }

    fun subscribe(): Flow<List<LibraryAnime>> {
        return animeRepository.getLibraryAnimeAsFlow()
            .retry {
                if (it is NullPointerException) {
                    delay(0.5.seconds)
                    true
                } else {
                    false
                }
            }.catch {
                this@GetLibraryAnime.logcat(LogPriority.ERROR, it)
            }
    }
}
