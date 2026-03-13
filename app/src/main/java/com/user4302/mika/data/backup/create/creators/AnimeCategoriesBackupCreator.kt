package com.user4302.mika.data.backup.create.creators

import com.user4302.mika.data.backup.models.BackupCategory
import com.user4302.mika.data.backup.models.backupCategoryMapper
import com.user4302.mika.domain.category.anime.interactor.GetAnimeCategories
import com.user4302.mika.domain.category.model.Category
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class AnimeCategoriesBackupCreator(
    private val getAnimeCategories: GetAnimeCategories = Injekt.get(),
) {

    suspend operator fun invoke(): List<BackupCategory> {
        return getAnimeCategories.await()
            .filterNot(Category::isSystemCategory)
            .map(backupCategoryMapper)
    }
}
