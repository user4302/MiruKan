package com.user4302.mika.ui.browse.manga.migration.search

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.user4302.mika.domain.entries.manga.interactor.GetManga
import com.user4302.mika.domain.entries.manga.model.Manga
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class MangaMigrateSearchScreenDialogScreenModel(
    val mangaId: Long,
    getManga: GetManga = Injekt.get(),
) : StateScreenModel<MangaMigrateSearchScreenDialogScreenModel.State>(State()) {

    init {
        screenModelScope.launch {
            val manga = getManga.await(mangaId)!!

            mutableState.update {
                it.copy(manga = manga)
            }
        }
    }

    fun setDialog(dialog: Dialog?) {
        mutableState.update {
            it.copy(dialog = dialog)
        }
    }

    @Immutable
    data class State(
        val manga: Manga? = null,
        val dialog: Dialog? = null,
    )

    sealed interface Dialog {
        data class Migrate(val manga: Manga) : Dialog
    }
}
