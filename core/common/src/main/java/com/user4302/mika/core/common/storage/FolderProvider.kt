package com.user4302.mika.core.common.storage

import java.io.File

interface FolderProvider {

    fun directory(): File

    fun path(): String
}
