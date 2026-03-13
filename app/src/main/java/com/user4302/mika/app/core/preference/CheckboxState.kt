package com.user4302.core.preference

import androidx.compose.ui.state.ToggleableState
import com.user4302.mika.core.common.preference.CheckboxState

fun <T> CheckboxState.TriState<T>.asToggleableState(): ToggleableState = when (this) {
    is CheckboxState.TriState.Exclude -> ToggleableState.Indeterminate
    is CheckboxState.TriState.Include -> ToggleableState.On
    is CheckboxState.TriState.None -> ToggleableState.Off
}
