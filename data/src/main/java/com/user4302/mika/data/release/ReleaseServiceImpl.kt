package com.user4302.mika.data.release

import android.os.Build
import com.user4302.mika.domain.release.interactor.GetApplicationRelease
import com.user4302.mika.domain.release.model.Release
import com.user4302.mika.domain.release.service.ReleaseService
import com.user4302.mika.network.GET
import com.user4302.mika.network.NetworkHelper
import com.user4302.mika.network.awaitSuccess
import com.user4302.mika.network.parseAs
import kotlinx.serialization.json.Json

class ReleaseServiceImpl(
    private val networkService: NetworkHelper,
    private val json: Json,
) : ReleaseService {

    override suspend fun latest(arguments: GetApplicationRelease.Arguments): Release? {
        val release = with(json) {
            networkService.client
                .newCall(GET("https://api.github.com/repos/${arguments.repository}/releases/latest"))
                .awaitSuccess()
                .parseAs<GithubRelease>()
        }
        val downloadLink = getDownloadLink(release = release) ?: return null

        return Release(
            version = release.version,
            info = release.info.replace(gitHubUsernameMentionRegex) { mention ->
                "[${mention.value}](https://github.com/${mention.value.substring(1)})"
            },
            releaseLink = release.releaseLink,
            downloadLink = downloadLink,
        )
    }

    private fun getDownloadLink(release: GithubRelease): String? {
        val map = release.assets.associate { asset ->
            BUILD_TYPES.find { "-$it" in asset.name } to asset.downloadLink
        }

        return map[Build.SUPPORTED_ABIS[0]] ?: map[null]
    }

    companion object {
        private val BUILD_TYPES = listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")

        /**
         * Regular expression that matches a mention to a valid GitHub username, like it's
         * done in GitHub Flavored Markdown. It follows these constraints:
         *
         * - Alphanumeric with single hyphens (no consecutive hyphens)
         * - Cannot begin or end with a hyphen
         * - Max length of 39 characters
         *
         * Reference: https://stackoverflow.com/a/30281147
         */
        private val gitHubUsernameMentionRegex = """\B@([a-z0-9](?:-(?=[a-z0-9])|[a-z0-9]){0,38}(?<=[a-z0-9]))"""
            .toRegex(RegexOption.IGNORE_CASE)
    }
}
