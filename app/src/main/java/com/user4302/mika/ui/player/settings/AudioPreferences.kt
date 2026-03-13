package com.user4302.mika.ui.player.settings

import com.user4302.mika.core.common.preference.PreferenceStore
import com.user4302.mika.core.common.preference.getEnum
import com.user4302.mika.i18n.MR
import dev.icerock.moko.resources.StringResource

class AudioPreferences(
    private val preferenceStore: PreferenceStore,
) {
    fun preferredAudioLanguages() = preferenceStore.getString("pref_audio_lang", "")
    fun enablePitchCorrection() = preferenceStore.getBoolean("pref_audio_pitch_correction", true)
    fun audioChannels() = preferenceStore.getEnum("pref_audio_config", AudioChannels.AutoSafe)
    fun volumeBoostCap() = preferenceStore.getInt("pref_audio_volume_boost_cap", 30)

    // Non-preferences

    fun audioDelay() = preferenceStore.getInt("pref_audio_delay", 0)
}

enum class AudioChannels(val titleRes: StringResource, val property: String, val value: String) {
    Auto(MR.strings.label_auto, "audio-channels", "auto-safe"),
    AutoSafe(MR.strings.label_auto, "audio-channels", "auto"),
    Mono(MR.strings.label_auto, "audio-channels", "mono"),
    Stereo(MR.strings.label_auto, "audio-channels", "stereo"),
    ReverseStereo(MR.strings.label_auto, "af", "pan=[stereo|c0=c1|c1=c0]"),
}
