package com.user4302.mika.domain.category.anime.interactor

import com.user4302.mika.domain.library.model.LibraryDisplayMode
import com.user4302.mika.domain.library.service.LibraryPreferences

class SetAnimeDisplayMode(
    private val preferences: LibraryPreferences,
) {

    fun await(display: LibraryDisplayMode) {
        preferences.displayMode().set(display)
    }
}
