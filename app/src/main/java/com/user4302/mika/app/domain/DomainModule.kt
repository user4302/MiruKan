package com.user4302.domain

import com.user4302.domain.download.anime.interactor.DeleteEpisodeDownload
import com.user4302.domain.download.manga.interactor.DeleteChapterDownload
import com.user4302.domain.entries.anime.interactor.SetAnimeViewerFlags
import com.user4302.domain.entries.anime.interactor.SyncSeasonsWithSource
import com.user4302.domain.entries.anime.interactor.UpdateAnime
import com.user4302.domain.entries.manga.interactor.GetExcludedScanlators
import com.user4302.domain.entries.manga.interactor.SetExcludedScanlators
import com.user4302.domain.entries.manga.interactor.SetMangaViewerFlags
import com.user4302.domain.entries.manga.interactor.UpdateManga
import com.user4302.domain.extension.anime.interactor.GetAnimeExtensionLanguages
import com.user4302.domain.extension.anime.interactor.GetAnimeExtensionSources
import com.user4302.domain.extension.anime.interactor.GetAnimeExtensionsByType
import com.user4302.domain.extension.anime.interactor.TrustAnimeExtension
import com.user4302.domain.extension.manga.interactor.GetExtensionSources
import com.user4302.domain.extension.manga.interactor.GetMangaExtensionLanguages
import com.user4302.domain.extension.manga.interactor.GetMangaExtensionsByType
import com.user4302.domain.extension.manga.interactor.TrustMangaExtension
import com.user4302.domain.items.chapter.interactor.GetAvailableScanlators
import com.user4302.domain.items.chapter.interactor.SetReadStatus
import com.user4302.domain.items.chapter.interactor.SyncChaptersWithSource
import com.user4302.domain.items.episode.interactor.SetSeenStatus
import com.user4302.domain.items.episode.interactor.SyncEpisodesWithSource
import com.user4302.domain.source.anime.interactor.GetAnimeIncognitoState
import com.user4302.domain.source.anime.interactor.GetAnimeSourcesWithFavoriteCount
import com.user4302.domain.source.anime.interactor.GetEnabledAnimeSources
import com.user4302.domain.source.anime.interactor.GetLanguagesWithAnimeSources
import com.user4302.domain.source.anime.interactor.ToggleAnimeIncognito
import com.user4302.domain.source.anime.interactor.ToggleAnimeSource
import com.user4302.domain.source.anime.interactor.ToggleAnimeSourcePin
import com.user4302.domain.source.interactor.SetMigrateSorting
import com.user4302.domain.source.interactor.ToggleLanguage
import com.user4302.domain.source.manga.interactor.GetEnabledMangaSources
import com.user4302.domain.source.manga.interactor.GetLanguagesWithMangaSources
import com.user4302.domain.source.manga.interactor.GetMangaIncognitoState
import com.user4302.domain.source.manga.interactor.GetMangaSourcesWithFavoriteCount
import com.user4302.domain.source.manga.interactor.ToggleMangaIncognito
import com.user4302.domain.source.manga.interactor.ToggleMangaSource
import com.user4302.domain.source.manga.interactor.ToggleMangaSourcePin
import com.user4302.domain.track.anime.interactor.AddAnimeTracks
import com.user4302.domain.track.anime.interactor.RefreshAnimeTracks
import com.user4302.domain.track.anime.interactor.SyncEpisodeProgressWithTrack
import com.user4302.domain.track.anime.interactor.TrackEpisode
import com.user4302.domain.track.manga.interactor.AddMangaTracks
import com.user4302.domain.track.manga.interactor.RefreshMangaTracks
import com.user4302.domain.track.manga.interactor.SyncChapterProgressWithTrack
import com.user4302.domain.track.manga.interactor.TrackChapter
import com.user4302.mika.data.category.anime.AnimeCategoryRepositoryImpl
import com.user4302.mika.data.category.manga.MangaCategoryRepositoryImpl
import com.user4302.mika.data.custombutton.CustomButtonRepositoryImpl
import com.user4302.mika.data.entries.anime.AnimeRepositoryImpl
import com.user4302.mika.data.entries.manga.MangaRepositoryImpl
import com.user4302.mika.data.history.anime.AnimeHistoryRepositoryImpl
import com.user4302.mika.data.history.manga.MangaHistoryRepositoryImpl
import com.user4302.mika.data.items.chapter.ChapterRepositoryImpl
import com.user4302.mika.data.items.episode.EpisodeRepositoryImpl
import com.user4302.mika.data.release.ReleaseServiceImpl
import com.user4302.mika.data.repository.anime.AnimeExtensionRepoRepositoryImpl
import com.user4302.mika.data.repository.manga.MangaExtensionRepoRepositoryImpl
import com.user4302.mika.data.source.anime.AnimeSourceRepositoryImpl
import com.user4302.mika.data.source.anime.AnimeStubSourceRepositoryImpl
import com.user4302.mika.data.source.manga.MangaSourceRepositoryImpl
import com.user4302.mika.data.source.manga.MangaStubSourceRepositoryImpl
import com.user4302.mika.data.track.anime.AnimeTrackRepositoryImpl
import com.user4302.mika.data.track.manga.MangaTrackRepositoryImpl
import com.user4302.mika.data.updates.anime.AnimeUpdatesRepositoryImpl
import com.user4302.mika.data.updates.manga.MangaUpdatesRepositoryImpl
import com.user4302.mika.domain.category.anime.interactor.CreateAnimeCategoryWithName
import com.user4302.mika.domain.category.anime.interactor.DeleteAnimeCategory
import com.user4302.mika.domain.category.anime.interactor.GetAnimeCategories
import com.user4302.mika.domain.category.anime.interactor.GetVisibleAnimeCategories
import com.user4302.mika.domain.category.anime.interactor.HideAnimeCategory
import com.user4302.mika.domain.category.anime.interactor.RenameAnimeCategory
import com.user4302.mika.domain.category.anime.interactor.ReorderAnimeCategory
import com.user4302.mika.domain.category.anime.interactor.ResetAnimeCategoryFlags
import com.user4302.mika.domain.category.anime.interactor.SetAnimeCategories
import com.user4302.mika.domain.category.anime.interactor.SetAnimeDisplayMode
import com.user4302.mika.domain.category.anime.interactor.SetSortModeForAnimeCategory
import com.user4302.mika.domain.category.anime.interactor.UpdateAnimeCategory
import com.user4302.mika.domain.category.anime.repository.AnimeCategoryRepository
import com.user4302.mika.domain.category.manga.interactor.CreateMangaCategoryWithName
import com.user4302.mika.domain.category.manga.interactor.DeleteMangaCategory
import com.user4302.mika.domain.category.manga.interactor.GetMangaCategories
import com.user4302.mika.domain.category.manga.interactor.GetVisibleMangaCategories
import com.user4302.mika.domain.category.manga.interactor.HideMangaCategory
import com.user4302.mika.domain.category.manga.interactor.RenameMangaCategory
import com.user4302.mika.domain.category.manga.interactor.ReorderMangaCategory
import com.user4302.mika.domain.category.manga.interactor.ResetMangaCategoryFlags
import com.user4302.mika.domain.category.manga.interactor.SetMangaCategories
import com.user4302.mika.domain.category.manga.interactor.SetMangaDisplayMode
import com.user4302.mika.domain.category.manga.interactor.SetSortModeForMangaCategory
import com.user4302.mika.domain.category.manga.interactor.UpdateMangaCategory
import com.user4302.mika.domain.category.manga.repository.MangaCategoryRepository
import com.user4302.mika.domain.custombuttons.interactor.CreateCustomButton
import com.user4302.mika.domain.custombuttons.interactor.DeleteCustomButton
import com.user4302.mika.domain.custombuttons.interactor.GetCustomButtons
import com.user4302.mika.domain.custombuttons.interactor.ReorderCustomButton
import com.user4302.mika.domain.custombuttons.interactor.ToggleFavoriteCustomButton
import com.user4302.mika.domain.custombuttons.interactor.UpdateCustomButton
import com.user4302.mika.domain.custombuttons.repository.CustomButtonRepository
import com.user4302.mika.domain.entries.anime.interactor.AnimeFetchInterval
import com.user4302.mika.domain.entries.anime.interactor.GetAnime
import com.user4302.mika.domain.entries.anime.interactor.GetAnimeByUrlAndSourceId
import com.user4302.mika.domain.entries.anime.interactor.GetAnimeFavorites
import com.user4302.mika.domain.entries.anime.interactor.GetAnimeWithEpisodesAndSeasons
import com.user4302.mika.domain.entries.anime.interactor.GetDuplicateLibraryAnime
import com.user4302.mika.domain.entries.anime.interactor.GetLibraryAnime
import com.user4302.mika.domain.entries.anime.interactor.NetworkToLocalAnime
import com.user4302.mika.domain.entries.anime.interactor.ResetAnimeViewerFlags
import com.user4302.mika.domain.entries.anime.interactor.SetAnimeEpisodeFlags
import com.user4302.mika.domain.entries.anime.interactor.SetAnimeSeasonFlags
import com.user4302.mika.domain.entries.anime.repository.AnimeRepository
import com.user4302.mika.domain.entries.manga.interactor.GetDuplicateLibraryManga
import com.user4302.mika.domain.entries.manga.interactor.GetLibraryManga
import com.user4302.mika.domain.entries.manga.interactor.GetManga
import com.user4302.mika.domain.entries.manga.interactor.GetMangaByUrlAndSourceId
import com.user4302.mika.domain.entries.manga.interactor.GetMangaFavorites
import com.user4302.mika.domain.entries.manga.interactor.GetMangaWithChapters
import com.user4302.mika.domain.entries.manga.interactor.MangaFetchInterval
import com.user4302.mika.domain.entries.manga.interactor.NetworkToLocalManga
import com.user4302.mika.domain.entries.manga.interactor.ResetMangaViewerFlags
import com.user4302.mika.domain.entries.manga.interactor.SetMangaChapterFlags
import com.user4302.mika.domain.entries.manga.repository.MangaRepository
import com.user4302.mika.domain.history.anime.interactor.GetAnimeHistory
import com.user4302.mika.domain.history.anime.interactor.GetNextEpisodes
import com.user4302.mika.domain.history.anime.interactor.RemoveAnimeHistory
import com.user4302.mika.domain.history.anime.interactor.UpsertAnimeHistory
import com.user4302.mika.domain.history.anime.repository.AnimeHistoryRepository
import com.user4302.mika.domain.history.manga.interactor.GetMangaHistory
import com.user4302.mika.domain.history.manga.interactor.GetNextChapters
import com.user4302.mika.domain.history.manga.interactor.GetTotalReadDuration
import com.user4302.mika.domain.history.manga.interactor.RemoveMangaHistory
import com.user4302.mika.domain.history.manga.interactor.UpsertMangaHistory
import com.user4302.mika.domain.history.manga.repository.MangaHistoryRepository
import com.user4302.mika.domain.items.chapter.interactor.GetChapter
import com.user4302.mika.domain.items.chapter.interactor.GetChapterByUrlAndMangaId
import com.user4302.mika.domain.items.chapter.interactor.GetChaptersByMangaId
import com.user4302.mika.domain.items.chapter.interactor.SetMangaDefaultChapterFlags
import com.user4302.mika.domain.items.chapter.interactor.ShouldUpdateDbChapter
import com.user4302.mika.domain.items.chapter.interactor.UpdateChapter
import com.user4302.mika.domain.items.chapter.repository.ChapterRepository
import com.user4302.mika.domain.items.episode.interactor.GetEpisode
import com.user4302.mika.domain.items.episode.interactor.GetEpisodeByUrlAndAnimeId
import com.user4302.mika.domain.items.episode.interactor.GetEpisodesByAnimeId
import com.user4302.mika.domain.items.episode.interactor.SetAnimeDefaultEpisodeFlags
import com.user4302.mika.domain.items.episode.interactor.ShouldUpdateDbEpisode
import com.user4302.mika.domain.items.episode.interactor.UpdateEpisode
import com.user4302.mika.domain.items.episode.repository.EpisodeRepository
import com.user4302.mika.domain.items.season.interactor.GetAnimeSeasonsByParentId
import com.user4302.mika.domain.items.season.interactor.SetAnimeDefaultSeasonFlags
import com.user4302.mika.domain.items.season.interactor.ShouldUpdateDbSeason
import com.user4302.mika.domain.release.interactor.GetApplicationRelease
import com.user4302.mika.domain.release.service.ReleaseService
import com.user4302.mika.domain.source.anime.interactor.GetAnimeSourcesWithNonLibraryAnime
import com.user4302.mika.domain.source.anime.interactor.GetRemoteAnime
import com.user4302.mika.domain.source.anime.repository.AnimeSourceRepository
import com.user4302.mika.domain.source.anime.repository.AnimeStubSourceRepository
import com.user4302.mika.domain.source.manga.interactor.GetMangaSourcesWithNonLibraryManga
import com.user4302.mika.domain.source.manga.interactor.GetRemoteManga
import com.user4302.mika.domain.source.manga.repository.MangaSourceRepository
import com.user4302.mika.domain.source.manga.repository.MangaStubSourceRepository
import com.user4302.mika.domain.track.anime.interactor.DeleteAnimeTrack
import com.user4302.mika.domain.track.anime.interactor.GetAnimeTracks
import com.user4302.mika.domain.track.anime.interactor.GetTracksPerAnime
import com.user4302.mika.domain.track.anime.interactor.InsertAnimeTrack
import com.user4302.mika.domain.track.anime.repository.AnimeTrackRepository
import com.user4302.mika.domain.track.manga.interactor.DeleteMangaTrack
import com.user4302.mika.domain.track.manga.interactor.GetMangaTracks
import com.user4302.mika.domain.track.manga.interactor.GetTracksPerManga
import com.user4302.mika.domain.track.manga.interactor.InsertMangaTrack
import com.user4302.mika.domain.track.manga.repository.MangaTrackRepository
import com.user4302.mika.domain.updates.anime.interactor.GetAnimeUpdates
import com.user4302.mika.domain.updates.anime.repository.AnimeUpdatesRepository
import com.user4302.mika.domain.updates.manga.interactor.GetMangaUpdates
import com.user4302.mika.domain.updates.manga.repository.MangaUpdatesRepository
import com.user4302.mika.ui.player.utils.TrackSelect
import mihon.domain.extensionrepo.anime.interactor.CreateAnimeExtensionRepo
import mihon.domain.extensionrepo.anime.interactor.DeleteAnimeExtensionRepo
import mihon.domain.extensionrepo.anime.interactor.GetAnimeExtensionRepo
import mihon.domain.extensionrepo.anime.interactor.GetAnimeExtensionRepoCount
import mihon.domain.extensionrepo.anime.interactor.ReplaceAnimeExtensionRepo
import mihon.domain.extensionrepo.anime.interactor.UpdateAnimeExtensionRepo
import mihon.domain.extensionrepo.anime.repository.AnimeExtensionRepoRepository
import mihon.domain.extensionrepo.manga.interactor.CreateMangaExtensionRepo
import mihon.domain.extensionrepo.manga.interactor.DeleteMangaExtensionRepo
import mihon.domain.extensionrepo.manga.interactor.GetMangaExtensionRepo
import mihon.domain.extensionrepo.manga.interactor.GetMangaExtensionRepoCount
import mihon.domain.extensionrepo.manga.interactor.ReplaceMangaExtensionRepo
import mihon.domain.extensionrepo.manga.interactor.UpdateMangaExtensionRepo
import mihon.domain.extensionrepo.manga.repository.MangaExtensionRepoRepository
import mihon.domain.extensionrepo.service.ExtensionRepoService
import com.user4302.mika.domain.items.chapter.interactor.FilterChaptersForDownload
import com.user4302.mika.domain.items.episode.interactor.FilterEpisodesForDownload
import mihon.domain.upcoming.anime.interactor.GetUpcomingAnime
import mihon.domain.upcoming.manga.interactor.GetUpcomingManga
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addFactory
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class DomainModule : InjektModule {

    override fun InjektRegistrar.registerInjectables() {
        addSingletonFactory<AnimeCategoryRepository> { AnimeCategoryRepositoryImpl(get()) }
        addFactory { GetAnimeCategories(get()) }
        addFactory { GetVisibleAnimeCategories(get()) }
        addFactory { ResetAnimeCategoryFlags(get(), get()) }
        addFactory { SetAnimeDisplayMode(get()) }
        addFactory { SetSortModeForAnimeCategory(get(), get()) }
        addFactory { CreateAnimeCategoryWithName(get(), get()) }
        addFactory { RenameAnimeCategory(get()) }
        addFactory { ReorderAnimeCategory(get()) }
        addFactory { UpdateAnimeCategory(get()) }
        addFactory { HideAnimeCategory(get()) }
        addFactory { DeleteAnimeCategory(get(), get(), get()) }

        addSingletonFactory<MangaCategoryRepository> { MangaCategoryRepositoryImpl(get()) }
        addFactory { GetMangaCategories(get()) }
        addFactory { GetVisibleMangaCategories(get()) }
        addFactory { ResetMangaCategoryFlags(get(), get()) }
        addFactory { SetMangaDisplayMode(get()) }
        addFactory { SetSortModeForMangaCategory(get(), get()) }
        addFactory { CreateMangaCategoryWithName(get(), get()) }
        addFactory { RenameMangaCategory(get()) }
        addFactory { ReorderMangaCategory(get()) }
        addFactory { UpdateMangaCategory(get()) }
        addFactory { HideMangaCategory(get()) }
        addFactory { DeleteMangaCategory(get(), get(), get()) }

        addSingletonFactory<AnimeRepository> { AnimeRepositoryImpl(get()) }
        addFactory { GetDuplicateLibraryAnime(get()) }
        addFactory { GetAnimeFavorites(get()) }
        addFactory { GetLibraryAnime(get()) }
        addFactory { GetAnimeWithEpisodesAndSeasons(get(), get()) }
        addFactory { GetAnimeByUrlAndSourceId(get()) }
        addFactory { GetAnime(get()) }
        addFactory { GetAnimeSeasonsByParentId(get()) }
        addFactory { GetNextEpisodes(get(), get(), get()) }
        addFactory { GetUpcomingAnime(get()) }
        addFactory { ResetAnimeViewerFlags(get()) }
        addFactory { SetAnimeEpisodeFlags(get()) }
        addFactory { SetAnimeSeasonFlags(get()) }
        addFactory { AnimeFetchInterval(get()) }
        addFactory { SetAnimeDefaultEpisodeFlags(get(), get(), get()) }
        addFactory { SetAnimeDefaultSeasonFlags(get(), get(), get()) }
        addFactory { SetAnimeViewerFlags(get()) }
        addFactory { NetworkToLocalAnime(get(), get()) }
        addFactory { UpdateAnime(get(), get()) }
        addFactory { SetAnimeCategories(get()) }
        addFactory { ShouldUpdateDbSeason() }
        addFactory { SyncSeasonsWithSource(get(), get(), get(), get(), get()) }

        addSingletonFactory<MangaRepository> { MangaRepositoryImpl(get()) }
        addFactory { GetDuplicateLibraryManga(get()) }
        addFactory { GetMangaFavorites(get()) }
        addFactory { GetLibraryManga(get()) }
        addFactory { GetMangaWithChapters(get(), get()) }
        addFactory { GetMangaByUrlAndSourceId(get()) }
        addFactory { GetManga(get()) }
        addFactory { GetNextChapters(get(), get(), get()) }
        addFactory { GetUpcomingManga(get()) }
        addFactory { ResetMangaViewerFlags(get()) }
        addFactory { SetMangaChapterFlags(get()) }
        addFactory { MangaFetchInterval(get()) }
        addFactory {
            SetMangaDefaultChapterFlags(
                    get(),
                    get(),
                    get(),
            )
        }
        addFactory { SetMangaViewerFlags(get()) }
        addFactory { NetworkToLocalManga(get()) }
        addFactory { UpdateManga(get(), get()) }
        addFactory { SetMangaCategories(get()) }
        addFactory { GetExcludedScanlators(get()) }
        addFactory { SetExcludedScanlators(get()) }

        addSingletonFactory<ReleaseService> { ReleaseServiceImpl(get(), get()) }
        addFactory { GetApplicationRelease(get(), get()) }

        addSingletonFactory<AnimeTrackRepository> { AnimeTrackRepositoryImpl(get()) }
        addFactory { TrackEpisode(get(), get(), get(), get()) }
        addFactory { AddAnimeTracks(get(), get(), get(), get()) }
        addFactory { RefreshAnimeTracks(get(), get(), get(), get()) }
        addFactory { DeleteAnimeTrack(get()) }
        addFactory { GetTracksPerAnime(get()) }
        addFactory { GetAnimeTracks(get()) }
        addFactory { InsertAnimeTrack(get()) }
        addFactory { SyncEpisodeProgressWithTrack(get(), get(), get()) }

        addSingletonFactory<MangaTrackRepository> { MangaTrackRepositoryImpl(get()) }
        addFactory { TrackChapter(get(), get(), get(), get()) }
        addFactory { AddMangaTracks(get(), get(), get(), get()) }
        addFactory { RefreshMangaTracks(get(), get(), get(), get()) }
        addFactory { DeleteMangaTrack(get()) }
        addFactory { GetTracksPerManga(get()) }
        addFactory { GetMangaTracks(get()) }
        addFactory { InsertMangaTrack(get()) }
        addFactory { SyncChapterProgressWithTrack(get(), get(), get()) }

        addSingletonFactory<EpisodeRepository> { EpisodeRepositoryImpl(get()) }
        addFactory { GetEpisode(get()) }
        addFactory { GetEpisodesByAnimeId(get()) }
        addFactory { GetEpisodeByUrlAndAnimeId(get()) }
        addFactory { UpdateEpisode(get()) }
        addFactory { SetSeenStatus(get(), get(), get(), get()) }
        addFactory { ShouldUpdateDbEpisode() }
        addFactory {
            SyncEpisodesWithSource(get(), get(), get(), get(), get(), get(), get(), get())
        }
        addFactory { FilterEpisodesForDownload(get(), get(), get()) }

        addSingletonFactory<ChapterRepository> { ChapterRepositoryImpl(get()) }
        addFactory { GetChapter(get()) }
        addFactory { GetChaptersByMangaId(get()) }
        addFactory { GetChapterByUrlAndMangaId(get()) }
        addFactory { UpdateChapter(get()) }
        addFactory { SetReadStatus(get(), get(), get(), get()) }
        addFactory { ShouldUpdateDbChapter() }
        addFactory {
            SyncChaptersWithSource(get(), get(), get(), get(), get(), get(), get(), get(), get())
        }
        addFactory { GetAvailableScanlators(get()) }
        addFactory { FilterChaptersForDownload(get(), get(), get()) }

        addSingletonFactory<AnimeHistoryRepository> { AnimeHistoryRepositoryImpl(get()) }
        addFactory { GetAnimeHistory(get()) }
        addFactory { UpsertAnimeHistory(get()) }
        addFactory { RemoveAnimeHistory(get()) }

        addFactory { DeleteEpisodeDownload(get(), get()) }

        addFactory { GetAnimeExtensionsByType(get(), get()) }
        addFactory { GetAnimeExtensionSources(get()) }
        addFactory { GetAnimeExtensionLanguages(get(), get()) }

        addSingletonFactory<MangaHistoryRepository> { MangaHistoryRepositoryImpl(get()) }
        addFactory { GetMangaHistory(get()) }
        addFactory { UpsertMangaHistory(get()) }
        addFactory { RemoveMangaHistory(get()) }
        addFactory { GetTotalReadDuration(get()) }

        addFactory { DeleteChapterDownload(get(), get()) }

        addFactory { GetMangaExtensionsByType(get(), get()) }
        addFactory { GetExtensionSources(get()) }
        addFactory { GetMangaExtensionLanguages(get(), get()) }

        addSingletonFactory<AnimeUpdatesRepository> { AnimeUpdatesRepositoryImpl(get()) }
        addFactory { GetAnimeUpdates(get()) }

        addSingletonFactory<MangaUpdatesRepository> { MangaUpdatesRepositoryImpl(get()) }
        addFactory { GetMangaUpdates(get()) }

        addSingletonFactory<AnimeSourceRepository> { AnimeSourceRepositoryImpl(get(), get()) }
        addSingletonFactory<AnimeStubSourceRepository> { AnimeStubSourceRepositoryImpl(get(), get()) }
        addFactory { GetEnabledAnimeSources(get(), get()) }
        addFactory { GetLanguagesWithAnimeSources(get(), get()) }
        addFactory { GetRemoteAnime(get()) }
        addFactory { GetAnimeSourcesWithFavoriteCount(get(), get()) }
        addFactory { GetAnimeSourcesWithNonLibraryAnime(get()) }
        addFactory { ToggleAnimeSource(get()) }
        addFactory { ToggleAnimeSourcePin(get()) }

        addSingletonFactory<MangaSourceRepository> { MangaSourceRepositoryImpl(get(), get()) }
        addSingletonFactory<MangaStubSourceRepository> { MangaStubSourceRepositoryImpl(get()) }
        addFactory { GetEnabledMangaSources(get(), get()) }
        addFactory { GetLanguagesWithMangaSources(get(), get()) }
        addFactory { GetRemoteManga(get()) }
        addFactory { GetMangaSourcesWithFavoriteCount(get(), get()) }
        addFactory { GetMangaSourcesWithNonLibraryManga(get()) }
        addFactory { SetMigrateSorting(get()) }
        addFactory { ToggleLanguage(get()) }
        addFactory { ToggleMangaSource(get()) }
        addFactory { ToggleMangaSourcePin(get()) }
        addFactory { TrustAnimeExtension(get(), get()) }
        addFactory { TrustMangaExtension(get(), get()) }

        addFactory { ExtensionRepoService(get(), get()) }

        addSingletonFactory<AnimeExtensionRepoRepository> {
            AnimeExtensionRepoRepositoryImpl(get())
        }
        addFactory { GetAnimeExtensionRepo(get()) }
        addFactory { GetAnimeExtensionRepoCount(get()) }
        addFactory { CreateAnimeExtensionRepo(get(), get()) }
        addFactory { DeleteAnimeExtensionRepo(get()) }
        addFactory { ReplaceAnimeExtensionRepo(get()) }
        addFactory { UpdateAnimeExtensionRepo(get(), get()) }
        addFactory { ToggleAnimeIncognito(get()) }
        addFactory { GetAnimeIncognitoState(get(), get(), get()) }

        addSingletonFactory<MangaExtensionRepoRepository> {
            MangaExtensionRepoRepositoryImpl(get())
        }
        addFactory { GetMangaExtensionRepo(get()) }
        addFactory { GetMangaExtensionRepoCount(get()) }
        addFactory { CreateMangaExtensionRepo(get(), get()) }
        addFactory { DeleteMangaExtensionRepo(get()) }
        addFactory { ReplaceMangaExtensionRepo(get()) }
        addFactory { UpdateMangaExtensionRepo(get(), get()) }
        addFactory { ToggleMangaIncognito(get()) }
        addFactory { GetMangaIncognitoState(get(), get(), get()) }

        addSingletonFactory<CustomButtonRepository> { CustomButtonRepositoryImpl(get()) }
        addFactory { CreateCustomButton(get()) }
        addFactory { DeleteCustomButton(get()) }
        addFactory { GetCustomButtons(get()) }
        addFactory { UpdateCustomButton(get()) }
        addFactory { ReorderCustomButton(get()) }
        addFactory { ToggleFavoriteCustomButton(get()) }

        addFactory { TrackSelect(get(), get()) }
    }
}
