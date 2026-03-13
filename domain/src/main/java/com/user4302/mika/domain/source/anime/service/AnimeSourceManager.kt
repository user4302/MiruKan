package com.user4302.mika.domain.source.anime.service

import com.user4302.mika.animesource.AnimeCatalogueSource
import com.user4302.mika.animesource.AnimeSource
import com.user4302.mika.animesource.online.AnimeHttpSource
import com.user4302.mika.domain.source.anime.model.StubAnimeSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AnimeSourceManager {

    val isInitialized: StateFlow<Boolean>

    val catalogueSources: Flow<List<AnimeCatalogueSource>>

    fun get(sourceKey: Long): AnimeSource?

    fun getOrStub(sourceKey: Long): AnimeSource

    fun getOnlineSources(): List<AnimeHttpSource>

    fun getCatalogueSources(): List<AnimeCatalogueSource>

    fun getStubSources(): List<StubAnimeSource>
}
