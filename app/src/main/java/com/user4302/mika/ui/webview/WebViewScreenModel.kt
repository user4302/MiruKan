package com.user4302.mika.ui.webview

import android.content.Context
import androidx.core.net.toUri
import cafe.adriel.voyager.core.model.StateScreenModel
import com.user4302.mika.animesource.online.AnimeHttpSource
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.source.anime.service.AnimeSourceManager
import com.user4302.mika.domain.source.manga.service.MangaSourceManager
import com.user4302.mika.network.NetworkHelper
import com.user4302.mika.source.online.HttpSource
import com.user4302.mika.util.system.openInBrowser
import com.user4302.mika.util.system.toShareIntent
import com.user4302.mika.util.system.toast
import com.user4302.presentation.more.stats.StatsScreenState
import logcat.LogPriority
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class WebViewScreenModel(
    val sourceId: Long?,
    private val MangaSourceManager: MangaSourceManager = Injekt.get(),
    private val AnimeSourceManager: AnimeSourceManager = Injekt.get(),
    private val network: NetworkHelper = Injekt.get(),
) : StateScreenModel<StatsScreenState>(StatsScreenState.Loading) {

    var headers = emptyMap<String, String>()

    init {
        sourceId?.let { MangaSourceManager.get(it) as? HttpSource }?.let { mangasource ->
            try {
                headers = mangasource.headers.toMultimap().mapValues { it.value.getOrNull(0) ?: "" }
            } catch (e: Exception) {
                logcat(LogPriority.ERROR, e) { "Failed to build headers" }
            }
        }
        sourceId?.let { AnimeSourceManager.get(it) as? AnimeHttpSource }?.let { animesource ->
            try {
                headers = animesource.headers.toMultimap().mapValues { it.value.getOrNull(0) ?: "" }
            } catch (e: Exception) {
                logcat(LogPriority.ERROR, e) { "Failed to build headers" }
            }
        }
    }

    fun shareWebpage(context: Context, url: String) {
        try {
            context.startActivity(url.toUri().toShareIntent(context, type = "text/plain"))
        } catch (e: Exception) {
            context.toast(e.message)
        }
    }

    fun openInBrowser(context: Context, url: String) {
        context.openInBrowser(url, forceDefaultBrowser = true)
    }

    fun clearCookies(url: String) {
        url.toHttpUrlOrNull()?.let {
            val cleared = network.cookieJar.remove(it)
            logcat { "Cleared $cleared cookies for: $url" }
        }
    }
}
