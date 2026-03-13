package com.user4302.presentation.more.settings.screen.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.presentation.core.util.collectAsState
import com.user4302.mika.ui.player.settings.AudioChannels
import com.user4302.mika.ui.player.settings.AudioPreferences
import com.user4302.presentation.more.settings.Preference
import com.user4302.presentation.more.settings.screen.SearchableSettings
import kotlinx.collections.immutable.toImmutableMap
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.util.Locale
import java.util.MissingResourceException

object PlayerSettingsAudioScreen : SearchableSettings {

    @ReadOnlyComposable
    @Composable
    override fun getTitleRes() = AYAYMR.strings.pref_player_audio

    @Composable
    override fun getPreferences(): List<Preference> {
        val audioPreferences = remember { Injekt.get<AudioPreferences>() }

        val prefLangs = audioPreferences.preferredAudioLanguages()
        val pitchCorrection = audioPreferences.enablePitchCorrection()
        val audioChannels = audioPreferences.audioChannels()
        val boostCapPref = audioPreferences.volumeBoostCap()
        val boostCap by boostCapPref.collectAsState()

        return listOf(
            Preference.PreferenceItem.EditTextInfoPreference(
                preference = prefLangs,
                dialogSubtitle = stringResource(AYAYMR.strings.pref_player_audio_lang_info),
                title = stringResource(AYAYMR.strings.pref_player_audio_lang),
                validate = { pref ->
                    val langs = pref.split(",").filter(String::isNotEmpty).map(String::trim)
                    langs.forEach {
                        try {
                            val locale = Locale(it)
                            if (locale.isO3Language == locale.language &&
                                locale.language == locale.getDisplayName(Locale.ENGLISH)
                            ) {
                                throw MissingResourceException("", "", "")
                            }
                        } catch (_: MissingResourceException) {
                            return@EditTextInfoPreference false
                        }
                    }

                    true
                },
                errorMessage = { pref ->
                    val langs = pref.split(",").filter(String::isNotEmpty).map(String::trim)
                    langs.forEach {
                        try {
                            val locale = Locale(it)
                            if (locale.isO3Language == locale.language &&
                                locale.language == locale.getDisplayName(Locale.ENGLISH)
                            ) {
                                throw MissingResourceException("", "", "")
                            }
                        } catch (_: MissingResourceException) {
                            return@EditTextInfoPreference stringResource(
                                AYAYMR.strings.pref_player_subtitle_invalid_lang,
                                it,
                            )
                        }
                    }
                    ""
                },
            ),
            Preference.PreferenceItem.SwitchPreference(
                preference = pitchCorrection,
                title = stringResource(AYAYMR.strings.pref_player_audio_pitch_correction),
                subtitle = stringResource(AYAYMR.strings.pref_player_audio_pitch_correction_summary),
            ),
            Preference.PreferenceItem.ListPreference(
                preference = audioChannels,
                entries = AudioChannels.entries.associateWith {
                    stringResource(it.titleRes)
                }.toImmutableMap(),
                title = stringResource(AYAYMR.strings.pref_player_audio_channels),
            ),
            Preference.PreferenceItem.SliderPreference(
                value = boostCap,
                valueRange = 0..200,
                title = stringResource(AYAYMR.strings.pref_player_audio_boost_cap),
                subtitle = boostCap.toString(),
                onValueChanged = {
                    boostCapPref.set(it)
                    true
                },
            ),
        )
    }
}
