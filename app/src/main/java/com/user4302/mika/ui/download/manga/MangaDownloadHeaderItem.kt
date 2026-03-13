package com.user4302.mika.ui.download.manga

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.user4302.mika.R
import com.user4302.mika.data.download.manga.model.MangaDownload
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible

data class MangaDownloadHeaderItem(
    val id: Long,
    val name: String,
    val size: Int,
) : AbstractExpandableHeaderItem<MangaDownloadHeaderHolder, MangaDownloadItem>() {

    override fun getLayoutRes(): Int {
        return R.layout.download_header
    }

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
    ): MangaDownloadHeaderHolder {
        return MangaDownloadHeaderHolder(view, adapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: MangaDownloadHeaderHolder,
        position: Int,
        payloads: List<Any?>?,
    ) {
        holder.bind(this)
    }

    override fun isDraggable(): Boolean {
        return subItems?.any { it.download.status == MangaDownload.State.DOWNLOADING } != true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MangaDownloadHeaderItem) return false
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
