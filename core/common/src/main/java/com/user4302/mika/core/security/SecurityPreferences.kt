package com.user4302.mika.core.security

import com.user4302.mika.core.common.preference.Preference
import com.user4302.mika.core.common.preference.PreferenceStore
import com.user4302.mika.core.common.preference.getEnum
import com.user4302.mika.i18n.AYMR
import dev.icerock.moko.resources.StringResource

class SecurityPreferences(
    private val preferenceStore: PreferenceStore,
) {

    fun useAuthenticator() = preferenceStore.getBoolean("use_biometric_lock", false)

    fun lockAppAfter() = preferenceStore.getInt("lock_app_after", 0)

    fun secureScreen() = preferenceStore.getEnum("secure_screen_v2", SecureScreenMode.INCOGNITO)

    fun hideNotificationContent() = preferenceStore.getBoolean("hide_notification_content", false)

    /**
     * For app lock. Will be set when there is a pending timed lock.
     * Otherwise this pref should be deleted.
     */
    fun lastAppClosed() = preferenceStore.getLong(
        Preference.appStateKey("last_app_closed"),
        0,
    )

    enum class SecureScreenMode(val titleRes: StringResource) {
        ALWAYS(AYMR.strings.lock_always),
        INCOGNITO(AYMR.strings.pref_incognito_mode),
        NEVER(AYMR.strings.lock_never),
    }
}
