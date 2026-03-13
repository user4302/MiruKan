package com.user4302.mika.ui.browse.manga.migration

import com.user4302.domain.entries.manga.model.hasCustomCover
import com.user4302.mika.data.cache.MangaCoverCache
import com.user4302.mika.data.download.manga.MangaDownloadCache
import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.i18n.MR
import dev.icerock.moko.resources.StringResource
import uy.kohesive.injekt.injectLazy

data class MangaMigrationFlag(
    val flag: Int,
    val isDefaultSelected: Boolean,
    val titleId: StringResource,
) {
    companion object {
        fun create(flag: Int, defaultSelectionMap: Int, titleId: StringResource): MangaMigrationFlag {
            return MangaMigrationFlag(
                flag = flag,
                isDefaultSelected = defaultSelectionMap and flag != 0,
                titleId = titleId,
            )
        }
    }
}

object MangaMigrationFlags {

    private const val CHAPTERS = 0b00001
    private const val CATEGORIES = 0b00010
    private const val CUSTOM_COVER = 0b01000
    private const val DELETE_DOWNLOADED = 0b10000

    private val coverCache: MangaCoverCache by injectLazy()
    private val downloadCache: MangaDownloadCache by injectLazy()

    fun hasChapters(value: Int): Boolean {
        return value and CHAPTERS != 0
    }

    fun hasCategories(value: Int): Boolean {
        return value and CATEGORIES != 0
    }

    fun hasCustomCover(value: Int): Boolean {
        return value and CUSTOM_COVER != 0
    }

    fun hasDeleteDownloaded(value: Int): Boolean {
        return value and DELETE_DOWNLOADED != 0
    }

    /** Returns information about applicable flags with default selections. */
    fun getFlags(manga: Manga?, defaultSelectedBitMap: Int): List<MangaMigrationFlag> {
        val flags = mutableListOf<MangaMigrationFlag>()
        flags += MangaMigrationFlag.create(CHAPTERS, defaultSelectedBitMap, MR.strings.chapters)
        flags += MangaMigrationFlag.create(CATEGORIES, defaultSelectedBitMap, MR.strings.categories)

        if (manga != null) {
            if (manga.hasCustomCover(coverCache)) {
                flags += MangaMigrationFlag.create(
                    CUSTOM_COVER,
                    defaultSelectedBitMap,
                    MR.strings.custom_cover,
                )
            }
            if (downloadCache.getDownloadCount(manga) > 0) {
                flags += MangaMigrationFlag.create(
                    DELETE_DOWNLOADED,
                    defaultSelectedBitMap,
                    MR.strings.delete_downloaded,
                )
            }
        }
        return flags
    }

    /** Returns a bit map of selected flags. */
    fun getSelectedFlagsBitMap(
        selectedFlags: List<Boolean>,
        flags: List<MangaMigrationFlag>,
    ): Int {
        return selectedFlags
            .zip(flags)
            .filter { (isSelected, _) -> isSelected }
            .map { (_, flag) -> flag.flag }
            .reduceOrNull { acc, mask -> acc or mask } ?: 0
    }
}
