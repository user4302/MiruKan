package com.user4302.mika.ui.player.settings

import com.user4302.mika.core.common.preference.PreferenceStore

class AdvancedPlayerPreferences(
    private val preferenceStore: PreferenceStore,
) {
    fun mpvUserFiles() = preferenceStore.getBoolean("mpv_scripts", false)
    fun mpvConf() = preferenceStore.getString("pref_mpv_conf", "")
    fun mpvInput() = preferenceStore.getString("pref_mpv_input", "")

    // Non-preference

    fun playerStatisticsPage() = preferenceStore.getInt("pref_player_statistics_page", 0)
}
