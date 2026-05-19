package eu.kanade.tachiyomi.ui.download.anime

import android.view.MenuItem
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem

/**
 * Adapter storing a list of downloads.
 *
 * @param downloadItemListener Listener called when an item of the list is released.
 */
class AnimeDownloadAdapter(val downloadItemListener: DownloadItemListener) : FlexibleAdapter<AbstractFlexibleItem<*>>(
    null,
    downloadItemListener,
    true,
) {

    override fun shouldMove(fromPosition: Int, toPosition: Int): Boolean {
        val item = getItem(fromPosition)
        if (item?.isDraggable != true) {
            return false
        }

        val targetItem = getItem(toPosition)
        if (targetItem?.isDraggable != true) {
            return false
        }

        return true
    }

    interface DownloadItemListener {
        fun onItemReleased(position: Int)
        fun onMenuItemClick(position: Int, menuItem: MenuItem)
    }
}
