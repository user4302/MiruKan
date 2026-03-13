package com.user4302.mika.core.common.storage

import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.user4302.mika.core.common.i18n.stringResource
import com.user4302.mika.i18n.AYMR
import java.io.File

class AndroidStorageFolderProvider(
    private val context: Context,
) : FolderProvider {

    override fun directory(): File {
        return File(
            Environment.getExternalStorageDirectory().absolutePath + File.separator +
                context.stringResource(AYMR.strings.app_name),
        )
    }

    override fun path(): String {
        return directory().toUri().toString()
    }
}
