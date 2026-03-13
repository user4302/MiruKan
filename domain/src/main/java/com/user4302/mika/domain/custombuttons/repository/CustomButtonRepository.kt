package com.user4302.mika.domain.custombuttons.repository

import com.user4302.mika.domain.custombuttons.model.CustomButton
import com.user4302.mika.domain.custombuttons.model.CustomButtonUpdate
import kotlinx.coroutines.flow.Flow

interface CustomButtonRepository {

    fun subscribeAll(): Flow<List<CustomButton>>

    suspend fun getAll(): List<CustomButton>

    suspend fun insertCustomButton(
        name: String,
        sortIndex: Long,
        content: String,
        longPressContent: String,
        onStartup: String,
    )

    suspend fun updatePartialCustomButton(update: CustomButtonUpdate)

    suspend fun updatePartialCustomButtons(updates: List<CustomButtonUpdate>)

    suspend fun deleteCustomButton(customButtonId: Long)
}
