package com.user4302.mika.ui.download.anime

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import com.user4302.mika.R
import com.user4302.mika.core.common.i18n.stringResource
import com.user4302.mika.data.download.anime.model.AnimeDownload
import com.user4302.mika.databinding.DownloadItemBinding
import com.user4302.mika.i18n.MR
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.util.view.popupMenu
import eu.davidea.viewholders.FlexibleViewHolder

/**
 * Class used to hold the data of a download.
 * All the elements from the layout file "download_item" are available in this class.
 *
 * @param view the inflated view for this holder.
 * @constructor creates a new download holder.
 */
class AnimeDownloadHolder(private val view: View, val adapter: AnimeDownloadAdapter) :
    FlexibleViewHolder(view, adapter) {

    private val binding = DownloadItemBinding.bind(view)

    init {
        setDragHandleView(binding.reorder)
        binding.menu.setOnClickListener { it.post { showPopupMenu(it) } }
    }

    private lateinit var download: AnimeDownload

    /**
     * Binds this holder with the given category.
     *
     * @param download the download to bind.
     */
    fun bind(download: AnimeDownload) {
        this.download = download
        // Update the chapter name.
        binding.chapterTitle.text = download.episode.name

        // Update the manga title
        binding.mangaFullTitle.text = download.anime.title

        // Update the progress bar and the number of downloaded pages
        val video = download.video
        if (video == null) {
            binding.downloadProgress.progress = 0
            binding.downloadProgress.max = 1
            binding.downloadProgressText.text = ""
        } else {
            binding.downloadProgress.max = 100
            notifyProgress()
            notifyDownloadedPages()
        }

        binding.reorder.visibility =
            if (download.status == AnimeDownload.State.DOWNLOADING) View.INVISIBLE else View.VISIBLE
    }

    /**
     * Updates the progress bar of the download.
     */
    fun notifyProgress() {
        if (binding.downloadProgress.max == 1) {
            binding.downloadProgress.max = 100
        }
        if (download.progress == 0) {
            binding.downloadProgress.isIndeterminate = true
        } else {
            binding.downloadProgress.isIndeterminate = false
            binding.downloadProgress.setProgressCompat(download.progress, true)
        }
        binding.reorder.visibility =
            if (download.status == AnimeDownload.State.DOWNLOADING) View.INVISIBLE else View.VISIBLE
    }

    /**
     * Updates the text field of the number of downloaded pages.
     */
    fun notifyDownloadedPages() {
        binding.downloadProgressText.text = if (download.progress == 0) {
            view.context.stringResource(MR.strings.update_check_notification_download_in_progress)
        } else {
            view.context.stringResource(AYMR.strings.episode_download_progress, download.progress)
        }
    }

    override fun onItemReleased(position: Int) {
        super.onItemReleased(position)
        adapter.downloadItemListener.onItemReleased(position)
        binding.container.isDragged = false
    }

    override fun onActionStateChanged(position: Int, actionState: Int) {
        super.onActionStateChanged(position, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            binding.container.isDragged = true
        }
    }

    private fun showPopupMenu(view: View) {
        view.popupMenu(
            menuRes = R.menu.download_single,
            initMenu = {
                val isDownloading = download.status == AnimeDownload.State.DOWNLOADING
                findItem(R.id.move_to_top).isVisible = bindingAdapterPosition > 1 && !isDownloading
                findItem(R.id.move_to_bottom).isVisible =
                    bindingAdapterPosition != adapter.itemCount - 1 &&
                    !isDownloading
                findItem(R.id.move_to_top_series).isVisible = !isDownloading
                findItem(R.id.move_to_bottom_series).isVisible = !isDownloading
            },
            onMenuItemClick = {
                adapter.downloadItemListener.onMenuItemClick(bindingAdapterPosition, this)
            },
        )
    }
}
