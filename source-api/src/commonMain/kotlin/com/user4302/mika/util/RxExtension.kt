package com.user4302.mika.util

import rx.Observable

expect suspend fun <T> Observable<T>.awaitSingle(): T
