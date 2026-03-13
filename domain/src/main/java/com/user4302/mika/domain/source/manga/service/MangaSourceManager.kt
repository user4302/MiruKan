package com.user4302.mika.domain.source.manga.service

import com.user4302.mika.domain.source.manga.model.StubMangaSource
import com.user4302.mika.source.CatalogueSource
import com.user4302.mika.source.MangaSource
import com.user4302.mika.source.online.HttpSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MangaSourceManager {

    val isInitialized: StateFlow<Boolean>

    val catalogueSources: Flow<List<CatalogueSource>>

    fun get(sourceKey: Long): MangaSource?

    fun getOrStub(sourceKey: Long): MangaSource

    fun getOnlineSources(): List<HttpSource>

    fun getCatalogueSources(): List<CatalogueSource>

    fun getStubSources(): List<StubMangaSource>
}
