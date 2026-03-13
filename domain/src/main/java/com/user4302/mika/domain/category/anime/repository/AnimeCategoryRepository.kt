package com.user4302.mika.domain.category.anime.repository

import com.user4302.mika.domain.category.model.Category
import com.user4302.mika.domain.category.model.CategoryUpdate
import kotlinx.coroutines.flow.Flow

interface AnimeCategoryRepository {

    suspend fun getAnimeCategory(id: Long): Category?

    suspend fun getAllAnimeCategories(): List<Category>

    suspend fun getAllVisibleAnimeCategories(): List<Category>

    fun getAllAnimeCategoriesAsFlow(): Flow<List<Category>>

    fun getAllVisibleAnimeCategoriesAsFlow(): Flow<List<Category>>

    suspend fun getCategoriesByAnimeId(animeId: Long): List<Category>

    suspend fun getVisibleCategoriesByAnimeId(animeId: Long): List<Category>

    fun getCategoriesByAnimeIdAsFlow(animeId: Long): Flow<List<Category>>

    fun getVisibleCategoriesByAnimeIdAsFlow(animeId: Long): Flow<List<Category>>

    suspend fun insertAnimeCategory(category: Category)

    suspend fun updatePartialAnimeCategory(update: CategoryUpdate)

    suspend fun updatePartialAnimeCategories(updates: List<CategoryUpdate>)

    suspend fun updateAllAnimeCategoryFlags(flags: Long?)

    suspend fun deleteAnimeCategory(categoryId: Long)
}
