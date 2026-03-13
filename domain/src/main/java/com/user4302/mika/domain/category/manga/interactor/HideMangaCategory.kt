package com.user4302.mika.domain.category.manga.interactor

import com.user4302.mika.core.common.util.lang.withNonCancellableContext
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.category.manga.repository.MangaCategoryRepository
import com.user4302.mika.domain.category.model.Category
import com.user4302.mika.domain.category.model.CategoryUpdate
import logcat.LogPriority

class HideMangaCategory(
    private val categoryRepository: MangaCategoryRepository,
) {

    suspend fun await(category: Category) = withNonCancellableContext {
        val update = CategoryUpdate(
            id = category.id,
            hidden = !category.hidden,
        )

        try {
            categoryRepository.updatePartialMangaCategory(update)
            Result.Success
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
