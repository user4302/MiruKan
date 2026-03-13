package com.user4302.domain.track.model

import com.user4302.mika.MR
import dev.icerock.moko.resources.StringResource

enum class AutoTrackState(val titleRes: StringResource) {
    ALWAYS(MR.strings.lock_always),
    ASK(MR.strings.default_category_summary),
    NEVER(MR.strings.lock_never),
}
