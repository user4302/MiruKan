package eu.kanade.tachiyomi.ui.download.anime

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import eu.kanade.tachiyomi.databinding.DownloadAccordionHeaderBinding

class AccordionHeaderHolder(view: View, val adapter: AnimeDownloadAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val binding = DownloadAccordionHeaderBinding.bind(view)

    fun bind(item: AccordionHeaderItem) {
        binding.title.text = item.title
        binding.count.text = item.count.toString()
        binding.root.setOnClickListener {
            adapter.downloadItemListener.onHeaderToggle(item.type)
        }
        binding.clear.setOnClickListener {
            adapter.downloadItemListener.onHeaderClear(item.type)
        }
    }
}
