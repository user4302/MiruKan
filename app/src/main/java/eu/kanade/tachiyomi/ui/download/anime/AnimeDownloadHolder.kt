package eu.kanade.tachiyomi.ui.download.anime

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import eu.davidea.viewholders.FlexibleViewHolder
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.download.anime.model.AnimeDownload
import eu.kanade.tachiyomi.databinding.DownloadItemBinding
import eu.kanade.tachiyomi.util.view.popupMenu
import tachiyomi.core.common.i18n.stringResource
import tachiyomi.i18n.MR
import tachiyomi.i18n.aniyomi.AYMR

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
        val context = view.context
        val progressText = if (download.progress == 0) {
            context.stringResource(MR.strings.update_check_notification_download_in_progress)
        } else {
            context.stringResource(AYMR.strings.episode_download_progress, download.progress)
        }

        if (download.bytesDownloaded > 0) {
            val downloadedSize = android.text.format.Formatter.formatFileSize(context, download.bytesDownloaded)
            binding.downloadProgressText.text = if (download.totalBytes > 0) {
                val totalSize = android.text.format.Formatter.formatFileSize(context, download.totalBytes)
                context.stringResource(
                    AYMR.strings.episode_download_progress_with_size,
                    progressText,
                    downloadedSize,
                    totalSize,
                )
            } else {
                context.stringResource(
                    AYMR.strings.episode_download_progress_with_downloaded_size,
                    progressText,
                    downloadedSize,
                )
            }
        } else {
            binding.downloadProgressText.text = progressText
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
                    bindingAdapterPosition != adapter.itemCount - 1 && !isDownloading
                findItem(R.id.move_to_top_series).isVisible = !isDownloading
                findItem(R.id.move_to_bottom_series).isVisible = !isDownloading
            },
            onMenuItemClick = {
                adapter.downloadItemListener.onMenuItemClick(bindingAdapterPosition, this)
            },
        )
    }
}
