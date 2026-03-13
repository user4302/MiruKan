package com.user4302.mika.domain.custombuttons.interactor

import com.user4302.mika.core.common.util.lang.withNonCancellableContext
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.custombuttons.model.CustomButtonUpdate
import com.user4302.mika.domain.custombuttons.repository.CustomButtonRepository
import logcat.LogPriority

class DeleteCustomButton(
    private val customButtonRepository: CustomButtonRepository,
) {
    suspend fun await(customButtonId: Long) = withNonCancellableContext {
        try {
            customButtonRepository.deleteCustomButton(customButtonId)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            return@withNonCancellableContext Result.InternalError(e)
        }

        val customButtons = customButtonRepository.getAll()
        val updates = customButtons.mapIndexed { index, customButton ->
            CustomButtonUpdate(
                id = customButton.id,
                sortIndex = index.toLong(),
            )
        }

        try {
            customButtonRepository.updatePartialCustomButtons(updates)
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
