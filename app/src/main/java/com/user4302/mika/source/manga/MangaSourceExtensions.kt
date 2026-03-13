package com.user4302.mika.source.manga

import android.graphics.drawable.Drawable
import com.user4302.domain.source.service.SourcePreferences
import com.user4302.mika.domain.source.manga.model.StubMangaSource
import com.user4302.mika.extension.manga.MangaExtensionManager
import com.user4302.mika.source.MangaSource
import com.user4302.mika.source.local.entries.manga.isLocal
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

fun MangaSource.icon(): Drawable? = Injekt.get<MangaExtensionManager>().getAppIconForSource(this.id)

fun MangaSource.getPreferenceKey(): String = "source_$id"

fun MangaSource.toStubSource(): StubMangaSource = StubMangaSource(id = id, lang = lang, name = name)

fun MangaSource.getNameForMangaInfo(): String {
    val preferences = Injekt.get<SourcePreferences>()
    val enabledLanguages = preferences.enabledLanguages().get()
        .filterNot { it in listOf("all", "other") }
    val hasOneActiveLanguages = enabledLanguages.size == 1
    val isInEnabledLanguages = lang in enabledLanguages
    return when {
        // For edge cases where user disables a source they got manga of in their library.
        hasOneActiveLanguages && !isInEnabledLanguages -> toString()
        // Hide the language tag when only one language is used.
        hasOneActiveLanguages && isInEnabledLanguages -> name
        else -> toString()
    }
}

fun MangaSource.isLocalOrStub(): Boolean = isLocal() || this is StubMangaSource
