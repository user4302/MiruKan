package com.user4302.presentation.more.settings.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.user4302.mika.core.common.i18n.stringResource
import com.user4302.mika.core.security.SecurityPreferences
import com.user4302.mika.presentation.core.i18n.pluralStringResource
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.presentation.core.util.collectAsState
import com.user4302.mika.util.system.AuthenticatorUtil.authenticate
import com.user4302.mika.util.system.AuthenticatorUtil.isAuthenticationSupported
import com.user4302.presentation.more.settings.Preference
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableMap
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

object SettingsSecurityScreen : SearchableSettings {

    @ReadOnlyComposable
    @Composable
    override fun getTitleRes() = AYMR.strings.pref_category_security

    @Composable
    override fun getPreferences(): List<Preference> {
        val context = LocalContext.current
        val securityPreferences = remember { Injekt.get<SecurityPreferences>() }
        val authSupported = remember { context.isAuthenticationSupported() }

        val useAuthPref = securityPreferences.useAuthenticator()
        val useAuth by useAuthPref.collectAsState()

        return listOf(
            Preference.PreferenceItem.SwitchPreference(
                preference = useAuthPref,
                title = stringResource(AYMR.strings.lock_with_biometrics),
                enabled = authSupported,
                onValueChanged = {
                    (context as FragmentActivity).authenticate(
                        title = context.stringResource(AYMR.strings.lock_with_biometrics),
                    )
                },
            ),
            Preference.PreferenceItem.ListPreference(
                preference = securityPreferences.lockAppAfter(),
                entries = LockAfterValues
                    .associateWith {
                        when (it) {
                            -1 -> stringResource(AYMR.strings.lock_never)
                            0 -> stringResource(AYMR.strings.lock_always)
                            else -> pluralStringResource(
                                AYMR.plurals.lock_after_mins,
                                count = it,
                                it,
                            )
                        }
                    }
                    .toImmutableMap(),
                title = stringResource(AYMR.strings.lock_when_idle),
                enabled = authSupported && useAuth,
                onValueChanged = {
                    (context as FragmentActivity).authenticate(
                        title = context.stringResource(AYMR.strings.lock_when_idle),
                    )
                },
            ),
            Preference.PreferenceItem.SwitchPreference(
                preference = securityPreferences.hideNotificationContent(),
                title = stringResource(AYMR.strings.hide_notification_content),
            ),
            Preference.PreferenceItem.ListPreference(
                preference = securityPreferences.secureScreen(),
                entries = SecurityPreferences.SecureScreenMode.entries
                    .associateWith { stringResource(it.titleRes) }
                    .toImmutableMap(),
                title = stringResource(AYMR.strings.secure_screen),
            ),
            Preference.PreferenceItem.InfoPreference(stringResource(AYMR.strings.secure_screen_summary)),
        )
    }
}

private val LockAfterValues = persistentListOf(
    0, // Always
    1,
    2,
    5,
    10,
    -1, // Never
)
