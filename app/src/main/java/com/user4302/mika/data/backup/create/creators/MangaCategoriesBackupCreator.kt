package com.user4302.mika.data.backup.create.creators

import com.user4302.mika.data.backup.models.BackupCategory
import com.user4302.mika.data.backup.models.backupCategoryMapper
import com.user4302.mika.domain.category.manga.interactor.GetMangaCategories
import com.user4302.mika.domain.category.model.Category
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class MangaCategoriesBackupCreator(
    private val getMangaCategories: GetMangaCategories = Injekt.get(),
) {

    suspend operator fun invoke(): List<BackupCategory> {
        return getMangaCategories.await()
            .filterNot(Category::isSystemCategory)
            .map(backupCategoryMapper)
    }
}
