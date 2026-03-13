package com.user4302.mika.domain.custombuttons.interactor

import com.user4302.mika.domain.custombuttons.model.CustomButton
import com.user4302.mika.domain.custombuttons.repository.CustomButtonRepository
import kotlinx.coroutines.flow.Flow

class GetCustomButtons(
    private val customButtonRepository: CustomButtonRepository,
) {
    fun subscribeAll(): Flow<List<CustomButton>> {
        return customButtonRepository.subscribeAll()
    }

    suspend fun getAll(): List<CustomButton> {
        return customButtonRepository.getAll()
    }
}
