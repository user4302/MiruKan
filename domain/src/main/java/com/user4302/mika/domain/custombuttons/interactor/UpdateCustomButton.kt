package com.user4302.mika.domain.custombuttons.interactor

import com.user4302.mika.core.common.util.lang.withNonCancellableContext
import com.user4302.mika.domain.custombuttons.model.CustomButtonUpdate
import com.user4302.mika.domain.custombuttons.repository.CustomButtonRepository

class UpdateCustomButton(
    private val customButtonRepository: CustomButtonRepository,
) {
    suspend fun await(update: CustomButtonUpdate) = withNonCancellableContext {
        try {
            customButtonRepository.updatePartialCustomButton(update)
        } catch (e: Exception) {
            Result.InternalError(e)
        }
    }

    suspend fun await(updates: List<CustomButtonUpdate>) = withNonCancellableContext {
        try {
            customButtonRepository.updatePartialCustomButtons(updates)
            Result.Success
        } catch (e: Exception) {
            Result.InternalError(e)
        }
    }

    sealed interface Result {
        data object Success : Result
        data class InternalError(val error: Throwable) : Result
    }
}
