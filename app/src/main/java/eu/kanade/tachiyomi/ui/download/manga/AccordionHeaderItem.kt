package eu.kanade.tachiyomi.ui.download.manga

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.kanade.tachiyomi.R

class AccordionHeaderItem(
    val type: AccordionType,
    val title: String,
    val count: Int,
    val expanded: Boolean,
) : AbstractFlexibleItem<AccordionHeaderHolder>() {

    override fun getLayoutRes(): Int = R.layout.download_accordion_header

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): AccordionHeaderHolder {
        return AccordionHeaderHolder(view, adapter as MangaDownloadAdapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<*>>,
        holder: AccordionHeaderHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        holder.bind(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AccordionHeaderItem) return false
        return type == other.type && expanded == other.expanded && count == other.count
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + expanded.hashCode()
        result = 31 * result + count
        return result
    }
}
