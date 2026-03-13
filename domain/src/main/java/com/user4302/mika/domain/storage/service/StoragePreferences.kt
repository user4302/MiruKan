package com.user4302.mika.domain.storage.service

import com.user4302.mika.core.common.preference.Preference
import com.user4302.mika.core.common.preference.PreferenceStore
import com.user4302.mika.core.common.storage.FolderProvider

class StoragePreferences(
    private val folderProvider: FolderProvider,
    private val preferenceStore: PreferenceStore,
) {

    fun baseStorageDirectory() = preferenceStore.getString(Preference.appStateKey("storage_dir"), folderProvider.path())
}
