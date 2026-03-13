package mihon.domain.upcoming.manga.interactor

import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.domain.entries.manga.repository.MangaRepository
import com.user4302.mika.source.model.SManga
import kotlinx.coroutines.flow.Flow

class GetUpcomingManga(
    private val mangaRepository: MangaRepository,
) {

    private val includedStatuses = setOf(
        SManga.ONGOING.toLong(),
        SManga.PUBLISHING_FINISHED.toLong(),
    )

    suspend fun subscribe(): Flow<List<Manga>> {
        return mangaRepository.getUpcomingManga(includedStatuses)
    }
}
