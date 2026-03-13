package com.user4302.presentation.category

import android.content.Context
import androidx.compose.runtime.Composable
import com.user4302.mika.core.common.i18n.stringResource
import com.user4302.mika.domain.category.model.Category
import com.user4302.mika.presentation.core.i18n.stringResource

val Category.visualName: String
    @Composable
    get() = when {
        isSystemCategory -> stringResource(AYMR.strings.label_default)
        else -> name
    }

fun Category.visualName(context: Context): String =
    when {
        isSystemCategory -> context.stringResource(AYMR.strings.label_default)
        else -> name
    }
