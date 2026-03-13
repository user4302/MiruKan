package com.user4302.mika.domain.category.anime.interactor

import com.user4302.mika.core.common.util.lang.withNonCancellableContext
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.category.anime.repository.AnimeCategoryRepository
import com.user4302.mika.domain.category.model.CategoryUpdate
import com.user4302.mika.domain.download.service.DownloadPreferences
import com.user4302.mika.domain.library.service.LibraryPreferences
import logcat.LogPriority

class DeleteAnimeCategory(
    private val categoryRepository: AnimeCategoryRepository,
    private val libraryPreferences: LibraryPreferences,
    private val downloadPreferences: DownloadPreferences,
) {

    suspend fun await(categoryId: Long) = withNonCancellableContext {
        try {
            categoryRepository.deleteAnimeCategory(categoryId)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            return@withNonCancellableContext Result.InternalError(e)
        }

        val categories = categoryRepository.getAllAnimeCategories()
        val updates = categories.mapIndexed { index, category ->
            CategoryUpdate(
                id = category.id,
                order = index.toLong(),
            )
        }

        val defaultCategory = libraryPreferences.defaultAnimeCategory().get()
        if (defaultCategory == categoryId.toInt()) {
            libraryPreferences.defaultAnimeCategory().delete()
        }

        val categoryPreferences = listOf(
            libraryPreferences.animeUpdateCategories(),
            libraryPreferences.animeUpdateCategoriesExclude(),
            downloadPreferences.removeExcludeAnimeCategories(),
            downloadPreferences.downloadNewEpisodeCategories(),
            downloadPreferences.downloadNewEpisodeCategoriesExclude(),
        )
        val categoryIdString = categoryId.toString()
        categoryPreferences.forEach { preference ->
            val ids = preference.get()
            if (categoryIdString !in ids) return@forEach
            preference.set(ids.minus(categoryIdString))
        }

        try {
            categoryRepository.updatePartialAnimeCategories(updates)
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
