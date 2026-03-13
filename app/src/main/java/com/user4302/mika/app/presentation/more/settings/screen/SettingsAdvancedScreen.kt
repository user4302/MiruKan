package com.user4302.presentation.more.settings.screen

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.Settings
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.net.toUri
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.user4302.domain.base.BasePreferences
import com.user4302.domain.extension.anime.interactor.TrustAnimeExtension
import com.user4302.domain.extension.manga.interactor.TrustMangaExtension
import com.user4302.domain.source.service.SourcePreferences
import com.user4302.domain.source.service.SourcePreferences.DataSaver
import com.user4302.mika.core.common.i18n.stringResource
import com.user4302.mika.core.common.util.lang.launchNonCancellable
import com.user4302.mika.core.common.util.lang.withUIContext
import com.user4302.mika.core.common.util.system.ImageUtil
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.data.download.anime.AnimeDownloadCache
import com.user4302.mika.data.download.manga.MangaDownloadCache
import com.user4302.mika.data.library.anime.AnimeLibraryUpdateJob
import com.user4302.mika.data.library.anime.AnimeMetadataUpdateJob
import com.user4302.mika.data.library.manga.MangaLibraryUpdateJob
import com.user4302.mika.data.library.manga.MangaMetadataUpdateJob
import com.user4302.mika.domain.entries.manga.interactor.ResetMangaViewerFlags
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.network.NetworkHelper
import com.user4302.mika.network.NetworkPreferences
import com.user4302.mika.network.PREF_DOH_360
import com.user4302.mika.network.PREF_DOH_ADGUARD
import com.user4302.mika.network.PREF_DOH_ALIDNS
import com.user4302.mika.network.PREF_DOH_CLOUDFLARE
import com.user4302.mika.network.PREF_DOH_CONTROLD
import com.user4302.mika.network.PREF_DOH_DNSPOD
import com.user4302.mika.network.PREF_DOH_GOOGLE
import com.user4302.mika.network.PREF_DOH_LIBREDNS
import com.user4302.mika.network.PREF_DOH_MULLVAD
import com.user4302.mika.network.PREF_DOH_NJALLA
import com.user4302.mika.network.PREF_DOH_QUAD101
import com.user4302.mika.network.PREF_DOH_QUAD9
import com.user4302.mika.network.PREF_DOH_SHECAN
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.presentation.core.util.collectAsState
import com.user4302.mika.ui.more.OnboardingScreen
import com.user4302.mika.util.CrashLogUtil
import com.user4302.mika.util.system.GLUtil
import com.user4302.mika.util.system.isReleaseBuildType
import com.user4302.mika.util.system.isShizukuInstalled
import com.user4302.mika.util.system.powerManager
import com.user4302.mika.util.system.setDefaultSettings
import com.user4302.mika.util.system.toast
import com.user4302.presentation.more.settings.Preference
import com.user4302.presentation.more.settings.screen.advanced.ClearAnimeDatabaseScreen
import com.user4302.presentation.more.settings.screen.advanced.ClearDatabaseScreen
import com.user4302.presentation.more.settings.screen.debug.DebugInfoScreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.launch
import logcat.LogPriority
import okhttp3.Headers
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.io.File

object SettingsAdvancedScreen : SearchableSettings {

    @ReadOnlyComposable
    @Composable
    override fun getTitleRes() = AYMR.strings.pref_category_advanced

    @Composable
    override fun getPreferences(): List<Preference> {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val basePreferences = remember { Injekt.get<BasePreferences>() }
        val networkPreferences = remember { Injekt.get<NetworkPreferences>() }

        return listOf(
            Preference.PreferenceItem.TextPreference(
                title = stringResource(AYMR.strings.pref_dump_crash_logs),
                subtitle = stringResource(AYMR.strings.pref_dump_crash_logs_summary),
                onClick = {
                    scope.launch {
                        CrashLogUtil(context).dumpLogs()
                    }
                },
            ),
            Preference.PreferenceItem.SwitchPreference(
                preference = networkPreferences.verboseLogging(),
                title = stringResource(AYMR.strings.pref_verbose_logging),
                subtitle = stringResource(AYMR.strings.pref_verbose_logging_summary),
                onValueChanged = {
                    context.toast(AYMR.strings.requires_app_restart)
                    true
                },
            ),
            Preference.PreferenceItem.TextPreference(
                title = stringResource(AYMR.strings.pref_debug_info),
                onClick = { navigator.push(DebugInfoScreen()) },
            ),
            Preference.PreferenceItem.TextPreference(
                title = stringResource(AYMR.strings.pref_onboarding_guide),
                onClick = { navigator.push(OnboardingScreen()) },
            ),
            Preference.PreferenceItem.TextPreference(
                title = stringResource(AYMR.strings.pref_manage_notifications),
                onClick = {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                },
            ),
            getBackgroundActivityGroup(),
            getDataGroup(),
            getNetworkGroup(networkPreferences = networkPreferences),
            getLibraryGroup(),
            getReaderGroup(basePreferences = basePreferences),
            getExtensionsGroup(basePreferences = basePreferences),
            // SY -->
            getDataSaverGroup(),
            // SY <--
        )
    }

    @Composable
    private fun getBackgroundActivityGroup(): Preference.PreferenceGroup {
        val context = LocalContext.current
        val uriHandler = LocalUriHandler.current

        return Preference.PreferenceGroup(
            title = stringResource(AYMR.strings.label_background_activity),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(AYMR.strings.pref_disable_battery_optimization),
                    subtitle = stringResource(AYMR.strings.pref_disable_battery_optimization_summary),
                    onClick = {
                        val packageName: String = context.packageName
                        if (!context.powerManager.isIgnoringBatteryOptimizations(packageName)) {
                            try {
                                @SuppressLint("BatteryLife")
                                val intent = Intent().apply {
                                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                                    data = "package:$packageName".toUri()
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                context.toast(AYMR.strings.battery_optimization_setting_activity_not_found)
                            }
                        } else {
                            context.toast(AYMR.strings.battery_optimization_disabled)
                        }
                    },
                ),
                Preference.PreferenceItem.TextPreference(
                    title = "Don't kill my app!",
                    subtitle = stringResource(AYMR.strings.about_dont_kill_my_app),
                    onClick = { uriHandler.openUri("https://dontkillmyapp.com/") },
                ),
            ),
        )
    }

    @Composable
    private fun getDataGroup(): Preference.PreferenceGroup {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        return Preference.PreferenceGroup(
            title = stringResource(AYMR.strings.label_data),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(AYMR.strings.pref_invalidate_download_cache),
                    subtitle = stringResource(AYAYMR.strings.pref_invalidate_download_cache_summary),
                    onClick = {
                        Injekt.get<MangaDownloadCache>().invalidateCache()
                        Injekt.get<AnimeDownloadCache>().invalidateCache()
                        context.toast(AYMR.strings.download_cache_invalidated)
                    },
                ),
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(AYAYMR.strings.pref_clear_manga_database),
                    subtitle = stringResource(AYAYMR.strings.pref_clear_manga_database_summary),
                    onClick = { navigator.push(ClearDatabaseScreen()) },
                ),
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(AYAYMR.strings.pref_clear_anime_database),
                    subtitle = stringResource(AYAYMR.strings.pref_clear_anime_database_summary),
                    onClick = { navigator.push(ClearAnimeDatabaseScreen()) },
                ),
            ),
        )
    }

    @Composable
    private fun getNetworkGroup(
        networkPreferences: NetworkPreferences,
    ): Preference.PreferenceGroup {
        val context = LocalContext.current
        val networkHelper = remember { Injekt.get<NetworkHelper>() }

        val userAgentPref = networkPreferences.defaultUserAgent()
        val userAgent by userAgentPref.collectAsState()

        return Preference.PreferenceGroup(
            title = stringResource(AYMR.strings.label_network),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(AYMR.strings.pref_clear_cookies),
                    onClick = {
                        networkHelper.cookieJar.removeAll()
                        context.toast(AYMR.strings.cookies_cleared)
                    },
                ),
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(AYMR.strings.pref_clear_webview_data),
                    onClick = {
                        try {
                            WebView(context).run {
                                setDefaultSettings()
                                clearCache(true)
                                clearFormData()
                                clearHistory()
                                clearSslPreferences()
                            }
                            WebStorage.getInstance().deleteAllData()
                            context.applicationInfo?.dataDir?.let { File("$it/app_webview/").deleteRecursively() }
                            context.toast(AYMR.strings.webview_data_deleted)
                        } catch (e: Throwable) {
                            logcat(LogPriority.ERROR, e)
                            context.toast(AYMR.strings.cache_delete_error)
                        }
                    },
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = networkPreferences.dohProvider(),
                    entries = persistentMapOf(
                        -1 to stringResource(AYMR.strings.disabled),
                        PREF_DOH_CLOUDFLARE to "Cloudflare",
                        PREF_DOH_GOOGLE to "Google",
                        PREF_DOH_ADGUARD to "AdGuard",
                        PREF_DOH_QUAD9 to "Quad9",
                        PREF_DOH_ALIDNS to "AliDNS",
                        PREF_DOH_DNSPOD to "DNSPod",
                        PREF_DOH_360 to "360",
                        PREF_DOH_QUAD101 to "Quad 101",
                        PREF_DOH_MULLVAD to "Mullvad",
                        PREF_DOH_CONTROLD to "Control D",
                        PREF_DOH_NJALLA to "Njalla",
                        PREF_DOH_SHECAN to "Shecan",
                        PREF_DOH_LIBREDNS to "LibreDNS",
                    ),
                    title = stringResource(AYMR.strings.pref_dns_over_https),
                    onValueChanged = {
                        context.toast(AYMR.strings.requires_app_restart)
                        true
                    },
                ),
                Preference.PreferenceItem.EditTextPreference(
                    preference = userAgentPref,
                    title = stringResource(AYMR.strings.pref_user_agent_string),
                    onValueChanged = {
                        try {
                            // OkHttp checks for valid values internally
                            Headers.Builder().add("User-Agent", it)
                            context.toast(AYMR.strings.requires_app_restart)
                        } catch (_: IllegalArgumentException) {
                            context.toast(AYMR.strings.error_user_agent_string_invalid)
                            return@EditTextPreference false
                        }
                        true
                    },
                ),
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(AYMR.strings.pref_reset_user_agent_string),
                    enabled = remember(userAgent) { userAgent != userAgentPref.defaultValue() },
                    onClick = {
                        userAgentPref.delete()
                        context.toast(AYMR.strings.requires_app_restart)
                    },
                ),
            ),
        )
    }

    @Composable
    private fun getLibraryGroup(): Preference.PreferenceGroup {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        return Preference.PreferenceGroup(
            title = stringResource(AYMR.strings.label_library),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(AYMR.strings.pref_refresh_library_covers),
                    onClick = {
                        AnimeLibraryUpdateJob.startNow(context)
                        MangaLibraryUpdateJob.startNow(context)
                        AnimeMetadataUpdateJob.startNow(context)
                        MangaMetadataUpdateJob.startNow(context)
                    },
                ),
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(AYMR.strings.pref_reset_viewer_flags),
                    subtitle = stringResource(AYMR.strings.pref_reset_viewer_flags_summary),
                    onClick = {
                        scope.launchNonCancellable {
                            val success = Injekt.get<ResetMangaViewerFlags>().await()
                            withUIContext {
                                val message = if (success) {
                                    AYMR.strings.pref_reset_viewer_flags_success
                                } else {
                                    AYMR.strings.pref_reset_viewer_flags_error
                                }
                                context.toast(message)
                            }
                        }
                    },
                ),
            ),
        )
    }

    @Composable
    private fun getReaderGroup(
        basePreferences: BasePreferences,
    ): Preference.PreferenceGroup {
        val context = LocalContext.current
        val chooseColorProfile = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
        ) { uri ->
            uri?.let {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flags)
                basePreferences.displayProfile().set(uri.toString())
            }
        }
        return Preference.PreferenceGroup(
            title = stringResource(AYMR.strings.pref_category_reader),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.ListPreference(
                    preference = basePreferences.hardwareBitmapThreshold(),
                    entries = GLUtil.CUSTOM_TEXTURE_LIMIT_OPTIONS
                        .mapIndexed { index, option ->
                            val display = if (index == 0) {
                                stringResource(AYMR.strings.pref_hardware_bitmap_threshold_default, option)
                            } else {
                                option.toString()
                            }
                            option to display
                        }
                        .toMap()
                        .toImmutableMap(),
                    title = stringResource(AYMR.strings.pref_hardware_bitmap_threshold),
                    subtitleProvider = { value, options ->
                        stringResource(AYMR.strings.pref_hardware_bitmap_threshold_summary, options[value].orEmpty())
                    },
                    enabled = !ImageUtil.HARDWARE_BITMAP_UNSUPPORTED &&
                        GLUtil.DEVICE_TEXTURE_LIMIT > GLUtil.SAFE_TEXTURE_LIMIT,
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = basePreferences.alwaysDecodeLongStripWithSSIV(),
                    title = stringResource(AYMR.strings.pref_always_decode_long_strip_with_ssiv_2),
                    subtitle = stringResource(AYMR.strings.pref_always_decode_long_strip_with_ssiv_summary),
                ),
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(AYMR.strings.pref_display_profile),
                    subtitle = basePreferences.displayProfile().get(),
                    onClick = {
                        chooseColorProfile.launch(arrayOf("*/*"))
                    },
                ),
            ),
        )
    }

    @Composable
    private fun getExtensionsGroup(
        basePreferences: BasePreferences,
    ): Preference.PreferenceGroup {
        val context = LocalContext.current
        val uriHandler = LocalUriHandler.current
        val extensionInstallerPref = basePreferences.extensionInstaller()
        var shizukuMissing by rememberSaveable { mutableStateOf(false) }
        val trustAnimeExtension = remember { Injekt.get<TrustAnimeExtension>() }
        val trustMangaExtension = remember { Injekt.get<TrustMangaExtension>() }

        if (shizukuMissing) {
            val dismiss = { shizukuMissing = false }
            AlertDialog(
                onDismissRequest = dismiss,
                title = { Text(text = stringResource(AYMR.strings.ext_installer_shizuku)) },
                text = {
                    Text(
                        text = stringResource(AYMR.strings.ext_installer_shizuku_unavailable_dialog),
                    )
                },
                dismissButton = {
                    TextButton(onClick = dismiss) {
                        Text(text = stringResource(AYMR.strings.action_cancel))
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            dismiss()
                            uriHandler.openUri("https://shizuku.rikka.app/download")
                        },
                    ) {
                        Text(text = stringResource(AYMR.strings.action_ok))
                    }
                },
            )
        }
        return Preference.PreferenceGroup(
            title = stringResource(AYMR.strings.label_extensions),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.ListPreference(
                    preference = extensionInstallerPref,
                    entries = extensionInstallerPref.entries
                        .filter {
                            // TODO: allow private option in stable versions once URL handling is more fleshed out
                            if (isReleaseBuildType) {
                                it != BasePreferences.ExtensionInstaller.PRIVATE
                            } else {
                                true
                            }
                        }
                        .associateWith { stringResource(it.titleRes) }
                        .toImmutableMap(),
                    title = stringResource(AYMR.strings.ext_installer_pref),
                    onValueChanged = {
                        if (it == BasePreferences.ExtensionInstaller.SHIZUKU &&
                            !context.isShizukuInstalled
                        ) {
                            shizukuMissing = true
                            false
                        } else {
                            true
                        }
                    },
                ),
                Preference.PreferenceItem.TextPreference(
                    title = stringResource(AYMR.strings.ext_revoke_trust),
                    onClick = {
                        trustMangaExtension.revokeAll()
                        trustAnimeExtension.revokeAll()
                        context.toast(AYMR.strings.requires_app_restart)
                    },
                ),
            ),
        )
    }

    // SY -->
    @Composable
    private fun getDataSaverGroup(): Preference.PreferenceGroup {
        val sourcePreferences = remember { Injekt.get<SourcePreferences>() }
        val dataSaver by sourcePreferences.dataSaver().collectAsState()
        return Preference.PreferenceGroup(
            title = stringResource(AYAYMR.strings.data_saver),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.ListPreference(
                    preference = sourcePreferences.dataSaver(),
                    entries = persistentMapOf(
                        DataSaver.NONE to stringResource(AYMR.strings.disabled),
                        DataSaver.BANDWIDTH_HERO to stringResource(AYAYMR.strings.bandwidth_hero),
                        DataSaver.WSRV_NL to stringResource(AYAYMR.strings.wsrv),
                        DataSaver.RESMUSH_IT to stringResource(AYAYMR.strings.resmush),
                    ),
                    title = stringResource(AYAYMR.strings.data_saver),
                    subtitle = stringResource(AYAYMR.strings.data_saver_summary),
                ),
                Preference.PreferenceItem.EditTextPreference(
                    preference = sourcePreferences.dataSaverServer(),
                    title = stringResource(AYAYMR.strings.bandwidth_data_saver_server),
                    subtitle = stringResource(AYAYMR.strings.data_saver_server_summary),
                    enabled = dataSaver == DataSaver.BANDWIDTH_HERO,
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = sourcePreferences.dataSaverDownloader(),
                    title = stringResource(AYAYMR.strings.data_saver_downloader),
                    enabled = dataSaver != DataSaver.NONE,
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = sourcePreferences.dataSaverIgnoreJpeg(),
                    title = stringResource(AYAYMR.strings.data_saver_ignore_jpeg),
                    enabled = dataSaver != DataSaver.NONE,
                ),
                Preference.PreferenceItem.SwitchPreference(
                    preference = sourcePreferences.dataSaverIgnoreGif(),
                    title = stringResource(AYAYMR.strings.data_saver_ignore_gif),
                    enabled = dataSaver != DataSaver.NONE,
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = sourcePreferences.dataSaverImageQuality(),
                    entries = listOf(
                        "10%",
                        "20%",
                        "40%",
                        "50%",
                        "70%",
                        "80%",
                        "90%",
                        "95%",
                    ).associateBy { it.trimEnd('%').toInt() }.toPersistentMap(),
                    title = stringResource(AYAYMR.strings.data_saver_image_quality),
                    subtitle = stringResource(AYAYMR.strings.data_saver_image_quality_summary),
                    enabled = dataSaver != DataSaver.NONE,
                ),
                kotlin.run {
                    val dataSaverImageFormatJpeg by sourcePreferences.dataSaverImageFormatJpeg().collectAsState()
                    Preference.PreferenceItem.SwitchPreference(
                        preference = sourcePreferences.dataSaverImageFormatJpeg(),
                        title = stringResource(AYAYMR.strings.data_saver_image_format),
                        subtitle = if (dataSaverImageFormatJpeg) {
                            stringResource(AYAYMR.strings.data_saver_image_format_summary_on)
                        } else {
                            stringResource(AYAYMR.strings.data_saver_image_format_summary_off)
                        },
                        enabled = dataSaver != DataSaver.NONE && dataSaver != DataSaver.RESMUSH_IT,
                    )
                },
                Preference.PreferenceItem.SwitchPreference(
                    preference = sourcePreferences.dataSaverColorBW(),
                    title = stringResource(AYAYMR.strings.data_saver_color_bw),
                    enabled = dataSaver == DataSaver.BANDWIDTH_HERO,
                ),
            ),
        )
    }
    // SY <--
}
