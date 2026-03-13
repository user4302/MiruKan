package com.user4302.mika.presentation.widget.entries.anime

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.LifecycleCoroutineScope
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.core.security.SecurityPreferences
import com.user4302.mika.domain.updates.anime.interactor.GetAnimeUpdates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import logcat.LogPriority

class AnimeWidgetManager(
    private val getUpdates: GetAnimeUpdates,
    private val securityPreferences: SecurityPreferences,
) {

    fun Context.init(scope: LifecycleCoroutineScope) {
        combine(
            getUpdates.subscribe(seen = false, after = BaseAnimeUpdatesGridGlanceWidget.DateLimit.toEpochMilli()),
            securityPreferences.useAuthenticator().changes(),
            transform = { a, b -> a to b },
        )
            .distinctUntilChanged { old, new ->
                old.second == new.second &&
                    old.first.map { it.episodeId }.toSet() == new.first.map { it.episodeId }.toSet()
            }
            .onEach {
                try {
                    AnimeUpdatesGridGlanceWidget().updateAll(this)
                    AnimeUpdatesGridCoverScreenGlanceWidget().updateAll(this)
                } catch (e: Exception) {
                    logcat(LogPriority.ERROR, e) { "Failed to update widget" }
                }
            }
            .flowOn(Dispatchers.Default)
            .launchIn(scope)
    }
}
