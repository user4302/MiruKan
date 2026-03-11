package eu.kanade.tachiyomi.ui.download.anime

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.download.anime.model.AnimeDownload

data class AnimeDownloadHeaderItem(
    val id: Long,
    val name: String,
    val size: Int,
) : AbstractExpandableHeaderItem<AnimeDownloadHeaderHolder, AnimeDownloadItem>() {

    override fun getLayoutRes(): Int {
        return R.layout.download_header
    }

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
    ): AnimeDownloadHeaderHolder {
        return AnimeDownloadHeaderHolder(view, adapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: AnimeDownloadHeaderHolder,
        position: Int,
        payloads: List<Any?>?,
    ) {
        holder.bind(this)
    }

    override fun isDraggable(): Boolean {
        return subItems?.any { it.download.status == AnimeDownload.State.DOWNLOADING } != true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnimeDownloadHeaderItem) return false
        return id == other.id && size == other.size
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    init {
        isHidden = false
        isExpanded = true
        isSelectable = false
    }
}
