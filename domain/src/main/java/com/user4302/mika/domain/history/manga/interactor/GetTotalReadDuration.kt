package com.user4302.mika.domain.history.manga.interactor

import com.user4302.mika.domain.history.manga.repository.MangaHistoryRepository

class GetTotalReadDuration(
    private val repository: MangaHistoryRepository,
) {

    suspend fun await(): Long {
        return repository.getTotalReadDuration()
    }
}
