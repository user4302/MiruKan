package com.user4302.domain.ui.model

import com.user4302.mika.MR
import dev.icerock.moko.resources.StringResource

enum class TabletUiMode(val titleRes: StringResource) {
    AUTOMATIC(MR.strings.automatic_background),
    ALWAYS(MR.strings.lock_always),
    LANDSCAPE(MR.strings.landscape),
    NEVER(MR.strings.lock_never),
}
