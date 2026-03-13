package com.user4302.presentation.more.settings.screen.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.ui.player.Debanding
import com.user4302.mika.ui.player.settings.DecoderPreferences
import com.user4302.presentation.more.settings.Preference
import com.user4302.presentation.more.settings.screen.SearchableSettings
import kotlinx.collections.immutable.toImmutableMap
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

object PlayerSettingsDecoderScreen : SearchableSettings {

    @ReadOnlyComposable
    @Composable
    override fun getTitleRes() = AYAYMR.strings.pref_player_decoder

    @Composable
    override fun getPreferences(): List<Preference> {
        val decoderPreferences = remember { Injekt.get<DecoderPreferences>() }

        val tryHw = decoderPreferences.tryHWDecoding()
        val useGpuNext = decoderPreferences.gpuNext()
        val debanding = decoderPreferences.videoDebanding()
        val yuv420p = decoderPreferences.useYUV420P()

        return listOf(
            Preference.PreferenceItem.SwitchPreference(
                preference = tryHw,
                title = stringResource(AYAYMR.strings.pref_try_hw),
            ),
            Preference.PreferenceItem.SwitchPreference(
                preference = useGpuNext,
                title = stringResource(AYAYMR.strings.pref_gpu_next_title),
                subtitle = stringResource(AYAYMR.strings.pref_gpu_next_subtitle),
            ),
            Preference.PreferenceItem.ListPreference(
                preference = debanding,
                entries = Debanding.entries.associateWith {
                    it.name
                    // stringResource(it.)
                }.toImmutableMap(),
                title = stringResource(AYAYMR.strings.pref_debanding_title),
            ),
            Preference.PreferenceItem.SwitchPreference(
                preference = yuv420p,
                title = stringResource(AYAYMR.strings.pref_use_yuv420p_title),
                subtitle = stringResource(AYAYMR.strings.pref_use_yuv420p_subtitle),
            ),
        )
    }
}
