package com.user4302.mika.ui.reader.viewer

import com.user4302.mika.data.database.models.manga.toDomainChapter
import com.user4302.mika.ui.reader.model.ReaderChapter
import com.user4302.mika.domain.items.chapter.service.calculateChapterGap as domainCalculateChapterGap

fun calculateChapterGap(higherReaderChapter: ReaderChapter?, lowerReaderChapter: ReaderChapter?): Int {
    return domainCalculateChapterGap(
        higherReaderChapter?.chapter?.toDomainChapter(),
        lowerReaderChapter?.chapter?.toDomainChapter(),
    )
}
