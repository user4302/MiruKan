package com.user4302.mika.domain.category.manga.interactor

import com.user4302.mika.domain.category.manga.repository.MangaCategoryRepository
import com.user4302.mika.domain.category.model.Category
import kotlinx.coroutines.flow.Flow

class GetVisibleMangaCategories(
    private val categoryRepository: MangaCategoryRepository,
) {
    fun subscribe(): Flow<List<Category>> {
        return categoryRepository.getAllVisibleMangaCategoriesAsFlow()
    }

    fun subscribe(mangaId: Long): Flow<List<Category>> {
        return categoryRepository.getVisibleCategoriesByMangaIdAsFlow(mangaId)
    }

    suspend fun await(): List<Category> {
        return categoryRepository.getAllVisibleMangaCategories()
    }

    suspend fun await(mangaId: Long): List<Category> {
        return categoryRepository.getVisibleCategoriesByMangaId(mangaId)
    }
}
