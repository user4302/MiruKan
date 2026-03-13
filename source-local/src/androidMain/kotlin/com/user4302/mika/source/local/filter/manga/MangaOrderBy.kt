package com.user4302.mika.source.local.filter.manga

import android.content.Context
import com.user4302.mika.core.common.i18n.stringResource
import com.user4302.mika.i18n.AYMR
import com.user4302.mika.source.model.Filter

sealed class MangaOrderBy(context: Context, selection: Selection) : Filter.Sort(
    context.stringResource(AYMR.strings.local_filter_order_by),
    arrayOf(context.stringResource(AYMR.strings.title), context.stringResource(AYMR.strings.date)),
    selection,
) {
    class Popular(context: Context) : MangaOrderBy(context, Selection(0, true))
    class Latest(context: Context) : MangaOrderBy(context, Selection(1, false))
}
