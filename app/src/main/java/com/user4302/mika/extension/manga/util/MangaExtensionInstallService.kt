package com.user4302.mika.extension.manga.util

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import com.user4302.domain.base.BasePreferences
import com.user4302.mika.R
import com.user4302.mika.core.common.i18n.stringResource
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.data.notification.Notifications
import com.user4302.mika.extension.manga.installer.InstallerManga
import com.user4302.mika.extension.manga.installer.PackageInstallerInstallerManga
import com.user4302.mika.extension.manga.installer.ShizukuInstallerManga
import com.user4302.mika.extension.manga.util.MangaExtensionInstaller.Companion.EXTRA_DOWNLOAD_ID
import com.user4302.mika.i18n.MR
import com.user4302.mika.util.system.getSerializableExtraCompat
import com.user4302.mika.util.system.notificationBuilder
import logcat.LogPriority

class MangaExtensionInstallService : Service() {

    private var installer: InstallerManga? = null

    override fun onCreate() {
        val notification = notificationBuilder(Notifications.CHANNEL_EXTENSIONS_UPDATE) {
            setSmallIcon(R.drawable.ic_ani)
            setAutoCancel(false)
            setOngoing(true)
            setShowWhen(false)
            setContentTitle(stringResource(MR.strings.ext_install_service_notif))
            setProgress(100, 100, true)
        }.build()
        startForeground(Notifications.ID_EXTENSION_INSTALLER, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val uri = intent?.data
        val id = intent?.getLongExtra(EXTRA_DOWNLOAD_ID, -1)?.takeIf { it != -1L }
        val installerUsed = intent?.getSerializableExtraCompat<BasePreferences.ExtensionInstaller>(
            EXTRA_INSTALLER,
        )
        if (uri == null || id == null || installerUsed == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        if (installer == null) {
            installer = when (installerUsed) {
                BasePreferences.ExtensionInstaller.PACKAGEINSTALLER -> PackageInstallerInstallerManga(
                    this,
                )
                BasePreferences.ExtensionInstaller.SHIZUKU -> ShizukuInstallerManga(this)
                else -> {
                    logcat(LogPriority.ERROR) { "Not implemented for installer $installerUsed" }
                    stopSelf()
                    return START_NOT_STICKY
                }
            }
        }
        installer!!.addToQueue(id, uri)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        installer?.onDestroy()
        installer = null
    }

    override fun onBind(i: Intent?): IBinder? = null

    companion object {
        private const val EXTRA_INSTALLER = "EXTRA_INSTALLER"

        fun getIntent(
            context: Context,
            downloadId: Long,
            uri: Uri,
            installer: BasePreferences.ExtensionInstaller,
        ): Intent {
            return Intent(context, MangaExtensionInstallService::class.java)
                .setDataAndType(uri, MangaExtensionInstaller.APK_MIME)
                .putExtra(EXTRA_DOWNLOAD_ID, downloadId)
                .putExtra(EXTRA_INSTALLER, installer)
        }
    }
}
