package com.user4302.mika.domain.category.anime.interactor

import com.user4302.mika.core.common.util.lang.withNonCancellableContext
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.category.anime.repository.AnimeCategoryRepository
import com.user4302.mika.domain.category.model.Category
import com.user4302.mika.domain.category.model.CategoryUpdate
import logcat.LogPriority

class HideAnimeCategory(
    private val categoryRepository: AnimeCategoryRepository,
) {

    suspend fun await(category: Category) = withNonCancellableContext {
        val update = CategoryUpdate(
            id = category.id,
            hidden = !category.hidden,
        )

        try {
            categoryRepository.updatePartialAnimeCategory(update)
            RenameAnimeCategory.Result.Success
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            Result.InternalError(e)
        }
    }

    sealed class Result {
        data object Success : Result()
        data class InternalError(val error: Throwable) : Result()
    }
}
