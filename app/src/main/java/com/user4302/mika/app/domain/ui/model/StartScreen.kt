package com.user4302.domain.ui.model

import com.user4302.mika.MR
import com.user4302.mika.ui.browse.BrowseTab
import com.user4302.mika.ui.history.HistoriesTab
import com.user4302.mika.ui.library.anime.AnimeLibraryTab
import com.user4302.mika.ui.library.manga.MangaLibraryTab
import com.user4302.mika.ui.updates.UpdatesTab
import com.user4302.presentation.util.Tab
import dev.icerock.moko.resources.StringResource

enum class StartScreen(val titleRes: StringResource, val tab: Tab) {
    ANIME(MR.strings.label_anime, AnimeLibraryTab),
    MANGA(MR.strings.manga, MangaLibraryTab),
    UPDATES(MR.strings.label_recent_updates, UpdatesTab),
    HISTORY(MR.strings.label_recent_manga, HistoriesTab),
    BROWSE(MR.strings.browse, BrowseTab),
}
