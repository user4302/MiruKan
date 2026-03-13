package com.user4302.mika.domain.category.anime.interactor

import com.user4302.mika.core.common.util.lang.withNonCancellableContext
import com.user4302.mika.domain.category.anime.repository.AnimeCategoryRepository
import com.user4302.mika.domain.category.model.CategoryUpdate

class UpdateAnimeCategory(
    private val categoryRepository: AnimeCategoryRepository,
) {

    suspend fun await(payload: CategoryUpdate): Result = withNonCancellableContext {
        try {
            categoryRepository.updatePartialAnimeCategory(payload)
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
