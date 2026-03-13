package com.user4302.mika.data.backup.create.creators

import com.user4302.mika.data.backup.models.BackupCustomButtons
import com.user4302.mika.data.backup.models.backupCustomButtonsMapper
import com.user4302.mika.domain.custombuttons.interactor.GetCustomButtons
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class CustomButtonBackupCreator(
    private val getCustomButtons: GetCustomButtons = Injekt.get(),
) {
    suspend operator fun invoke(): List<BackupCustomButtons> {
        return getCustomButtons.getAll()
            .map(backupCustomButtonsMapper)
    }
}
