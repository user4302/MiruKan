package mihon.domain.upcoming.anime.interactor

import com.user4302.mika.animesource.model.SAnime
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.domain.entries.anime.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow

class GetUpcomingAnime(
    private val animeRepository: AnimeRepository,
) {

    private val includedStatuses = setOf(
        SAnime.ONGOING.toLong(),
        SAnime.PUBLISHING_FINISHED.toLong(),
    )

    suspend fun subscribe(): Flow<List<Anime>> {
        return animeRepository.getUpcomingAnime(includedStatuses)
    }
}
