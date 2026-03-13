package com.user4302.mika.domain.category.manga.interactor

import com.user4302.mika.core.common.util.lang.withNonCancellableContext
import com.user4302.mika.domain.category.manga.repository.MangaCategoryRepository
import com.user4302.mika.domain.category.model.CategoryUpdate

class UpdateMangaCategory(
    private val categoryRepository: MangaCategoryRepository,
) {

    suspend fun await(payload: CategoryUpdate): Result = withNonCancellableContext {
        try {
            categoryRepository.updatePartialMangaCategory(payload)
            Result.Success
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    sealed interface Result {
        data object Success : Result
        data class Error(val error: Exception) : Result
    }
}
