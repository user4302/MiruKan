package com.user4302.mika.domain.category.manga.interactor

import com.user4302.mika.domain.category.manga.repository.MangaCategoryRepository
import com.user4302.mika.domain.library.model.plus
import com.user4302.mika.domain.library.service.LibraryPreferences

class ResetMangaCategoryFlags(
    private val preferences: LibraryPreferences,
    private val categoryRepository: MangaCategoryRepository,
) {

    suspend fun await() {
        val sort = preferences.mangaSortingMode().get()
        categoryRepository.updateAllMangaCategoryFlags(sort.type + sort.direction)
    }
}
