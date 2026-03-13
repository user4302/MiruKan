package com.user4302.domain.source.anime.interactor

import com.user4302.domain.source.service.SourcePreferences
import com.user4302.mika.core.common.preference.getAndSet

class ToggleAnimeIncognito(
    private val preferences: SourcePreferences,
) {
    fun await(extensions: String, enable: Boolean) {
        preferences.incognitoAnimeExtensions().getAndSet {
            if (enable) it.plus(extensions) else it.minus(extensions)
        }
    }
}
