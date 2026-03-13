package com.user4302.mika.domain.category.anime.interactor

import com.user4302.mika.domain.category.anime.repository.AnimeCategoryRepository
import com.user4302.mika.domain.library.model.plus
import com.user4302.mika.domain.library.service.LibraryPreferences

class ResetAnimeCategoryFlags(
    private val preferences: LibraryPreferences,
    private val categoryRepository: AnimeCategoryRepository,
) {

    suspend fun await() {
        val sort = preferences.animeSortingMode().get()
        categoryRepository.updateAllAnimeCategoryFlags(sort.type + sort.direction)
    }
}
