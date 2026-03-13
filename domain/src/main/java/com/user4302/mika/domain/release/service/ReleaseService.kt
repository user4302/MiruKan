package com.user4302.mika.domain.release.service

import com.user4302.mika.domain.release.interactor.GetApplicationRelease
import com.user4302.mika.domain.release.model.Release

interface ReleaseService {

    suspend fun latest(arguments: GetApplicationRelease.Arguments): Release?
}
