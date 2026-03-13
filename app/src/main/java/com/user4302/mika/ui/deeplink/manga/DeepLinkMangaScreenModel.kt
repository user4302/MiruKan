package com.user4302.mika.ui.deeplink.manga

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.user4302.domain.entries.manga.model.toDomainManga
import com.user4302.domain.entries.manga.model.toSManga
import com.user4302.domain.items.chapter.interactor.SyncChaptersWithSource
import com.user4302.mika.core.common.util.lang.launchIO
import com.user4302.mika.domain.entries.manga.interactor.GetMangaByUrlAndSourceId
import com.user4302.mika.domain.entries.manga.interactor.NetworkToLocalManga
import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.domain.items.chapter.interactor.GetChapterByUrlAndMangaId
import com.user4302.mika.domain.items.chapter.model.Chapter
import com.user4302.mika.domain.source.manga.service.MangaSourceManager
import com.user4302.mika.source.MangaSource
import com.user4302.mika.source.model.SChapter
import com.user4302.mika.source.model.SManga
import com.user4302.mika.source.online.ResolvableSource
import com.user4302.mika.source.online.UriType
import kotlinx.coroutines.flow.update
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class DeepLinkMangaScreenModel(
    query: String = "",
    private val sourceManager: MangaSourceManager = Injekt.get(),
    private val networkToLocalManga: NetworkToLocalManga = Injekt.get(),
    private val getChapterByUrlAndMangaId: GetChapterByUrlAndMangaId = Injekt.get(),
    private val getMangaByUrlAndSourceId: GetMangaByUrlAndSourceId = Injekt.get(),
    private val syncChaptersWithSource: SyncChaptersWithSource = Injekt.get(),
) : StateScreenModel<DeepLinkMangaScreenModel.State>(State.Loading) {

    init {
        screenModelScope.launchIO {
            val source = sourceManager.getCatalogueSources()
                .filterIsInstance<ResolvableSource>()
                .firstOrNull { it.getUriType(query) != UriType.Unknown }

            val manga = source?.getManga(query)?.let {
                getMangaFromSManga(it, source.id)
            }

            val chapter = if (source?.getUriType(query) == UriType.Chapter && manga != null) {
                source.getChapter(query)?.let { getChapterFromSChapter(it, manga, source) }
            } else {
                null
            }

            mutableState.update {
                if (manga == null) {
                    State.NoResults
                } else {
                    if (chapter == null) {
                        State.Result(manga)
                    } else {
                        State.Result(manga, chapter.id)
                    }
                }
            }
        }
    }

    private suspend fun getChapterFromSChapter(sChapter: SChapter, manga: Manga, source: MangaSource): Chapter? {
        val localChapter = getChapterByUrlAndMangaId.await(sChapter.url, manga.id)

        return if (localChapter == null) {
            val sourceChapters = source.getChapterList(manga.toSManga())
            val newChapters = syncChaptersWithSource.await(sourceChapters, manga, source, false)
            newChapters.find { it.url == sChapter.url }
        } else {
            localChapter
        }
    }

    private suspend fun getMangaFromSManga(sManga: SManga, sourceId: Long): Manga {
        return getMangaByUrlAndSourceId.await(sManga.url, sourceId)
            ?: networkToLocalManga.await(sManga.toDomainManga(sourceId))
    }

    sealed interface State {
        @Immutable
        data object Loading : State

        @Immutable
        data object NoResults : State

        @Immutable
        data class Result(val manga: Manga, val chapterId: Long? = null) : State
    }
}
