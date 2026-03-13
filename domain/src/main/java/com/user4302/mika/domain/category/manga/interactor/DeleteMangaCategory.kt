package com.user4302.mika.domain.category.manga.interactor

import com.user4302.mika.core.common.util.lang.withNonCancellableContext
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.category.manga.repository.MangaCategoryRepository
import com.user4302.mika.domain.category.model.CategoryUpdate
import com.user4302.mika.domain.download.service.DownloadPreferences
import com.user4302.mika.domain.library.service.LibraryPreferences
import logcat.LogPriority

class DeleteMangaCategory(
    private val categoryRepository: MangaCategoryRepository,
    private val libraryPreferences: LibraryPreferences,
    private val downloadPreferences: DownloadPreferences,
) {

    suspend fun await(categoryId: Long) = withNonCancellableContext {
        try {
            categoryRepository.deleteMangaCategory(categoryId)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            return@withNonCancellableContext Result.InternalError(e)
        }

        val categories = categoryRepository.getAllMangaCategories()
        val updates = categories.mapIndexed { index, category ->
            CategoryUpdate(
                id = category.id,
                order = index.toLong(),
            )
        }

        val defaultCategory = libraryPreferences.defaultMangaCategory().get()
        if (defaultCategory == categoryId.toInt()) {
            libraryPreferences.defaultMangaCategory().delete()
        }

        val categoryPreferences = listOf(
            libraryPreferences.mangaUpdateCategories(),
            libraryPreferences.mangaUpdateCategories(),
            downloadPreferences.removeExcludeCategories(),
            downloadPreferences.downloadNewChapterCategories(),
            downloadPreferences.downloadNewChapterCategoriesExclude(),
        )
        val categoryIdString = categoryId.toString()
        categoryPreferences.forEach { preference ->
            val ids = preference.get()
            if (categoryIdString !in ids) return@forEach
            preference.set(ids.minus(categoryIdString))
        }

        try {
            categoryRepository.updatePartialMangaCategories(updates)
            Result.Success
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            Result.InternalError(e)
        }
    }

    sealed interface Result {
        data object Success : Result
        data class InternalError(val error: Throwable) : Result
    }
}
