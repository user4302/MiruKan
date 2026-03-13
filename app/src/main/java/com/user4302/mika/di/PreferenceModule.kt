package com.user4302.mika.di

import android.app.Application
import com.user4302.domain.base.BasePreferences
import com.user4302.domain.source.service.SourcePreferences
import com.user4302.domain.track.service.TrackPreferences
import com.user4302.domain.ui.UiPreferences
import com.user4302.mika.core.common.preference.AndroidPreferenceStore
import com.user4302.mika.core.common.preference.PreferenceStore
import com.user4302.mika.core.common.storage.AndroidStorageFolderProvider
import com.user4302.mika.core.security.SecurityPreferences
import com.user4302.mika.domain.backup.service.BackupPreferences
import com.user4302.mika.domain.download.service.DownloadPreferences
import com.user4302.mika.domain.library.service.LibraryPreferences
import com.user4302.mika.domain.storage.service.StoragePreferences
import com.user4302.mika.network.NetworkPreferences
import com.user4302.mika.ui.player.settings.AdvancedPlayerPreferences
import com.user4302.mika.ui.player.settings.AudioPreferences
import com.user4302.mika.ui.player.settings.DecoderPreferences
import com.user4302.mika.ui.player.settings.GesturePreferences
import com.user4302.mika.ui.player.settings.PlayerPreferences
import com.user4302.mika.ui.player.settings.SubtitlePreferences
import com.user4302.mika.ui.reader.setting.ReaderPreferences
import com.user4302.mika.util.system.isDebugBuildType
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class PreferenceModule(val app: Application) : InjektModule {
    override fun InjektRegistrar.registerInjectables() {
        addSingletonFactory<PreferenceStore> {
            AndroidPreferenceStore(app)
        }
        addSingletonFactory {
            NetworkPreferences(
                preferenceStore = get(),
                verboseLogging = isDebugBuildType,
            )
        }
        addSingletonFactory {
            SourcePreferences(get())
        }
        addSingletonFactory {
            SecurityPreferences(get())
        }
        addSingletonFactory {
            LibraryPreferences(get())
        }
        addSingletonFactory {
            ReaderPreferences(get())
        }
        addSingletonFactory {
            PlayerPreferences(get())
        }
        addSingletonFactory {
            GesturePreferences(get())
        }
        addSingletonFactory {
            DecoderPreferences(get())
        }
        addSingletonFactory {
            SubtitlePreferences(get())
        }
        addSingletonFactory {
            AudioPreferences(get())
        }
        addSingletonFactory {
            AdvancedPlayerPreferences(get())
        }
        addSingletonFactory {
            TrackPreferences(get())
        }
        addSingletonFactory {
            DownloadPreferences(get())
        }
        addSingletonFactory {
            BackupPreferences(get())
        }
        addSingletonFactory {
            StoragePreferences(
                folderProvider = get<AndroidStorageFolderProvider>(),
                preferenceStore = get(),
            )
        }
        addSingletonFactory {
            UiPreferences(get())
        }
        addSingletonFactory {
            BasePreferences(app, get())
        }
    }
}
