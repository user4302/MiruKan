package mihon.feature.upcoming.manga

import com.user4302.mika.domain.entries.manga.model.Manga
import java.time.LocalDate

sealed interface UpcomingMangaUIModel {
    data class Header(val date: LocalDate, val mangaCount: Int) : UpcomingMangaUIModel
    data class Item(val manga: Manga) : UpcomingMangaUIModel
}
