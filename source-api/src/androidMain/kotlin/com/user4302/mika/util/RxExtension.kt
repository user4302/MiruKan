package com.user4302.mika.util

import com.user4302.mika.core.common.util.lang.awaitSingle
import rx.Observable

actual suspend fun <T> Observable<T>.awaitSingle(): T = awaitSingle()
