package com.user4302.mika.source.local.filter.anime

import android.content.Context
import com.user4302.mika.animesource.model.AnimeFilter
import com.user4302.mika.core.common.i18n.stringResource
import com.user4302.mika.i18n.AYMR

sealed class AnimeOrderBy(context: Context, selection: Selection) : AnimeFilter.Sort(
    context.stringResource(AYMR.strings.local_filter_order_by),
    arrayOf(context.stringResource(AYMR.strings.title), context.stringResource(AYMR.strings.date)),
    selection,
) {
    class Popular(context: Context) : AnimeOrderBy(context, Selection(0, true))
    class Latest(context: Context) : AnimeOrderBy(context, Selection(1, false))
}
