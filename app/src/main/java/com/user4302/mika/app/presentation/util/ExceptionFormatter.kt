package com.user4302.presentation.util

import android.content.Context
import com.user4302.mika.core.common.i18n.stringResource
import com.user4302.mika.domain.items.chapter.model.NoChaptersException
import com.user4302.mika.domain.items.episode.model.NoEpisodesException
import com.user4302.mika.domain.source.anime.model.AnimeSourceNotInstalledException
import com.user4302.mika.domain.source.manga.model.SourceNotInstalledException
import com.user4302.mika.network.HttpException
import com.user4302.mika.util.system.isOnline
import java.net.UnknownHostException

context(Context)
val Throwable.formattedMessage: String
    get() {
        when (this) {
            is HttpException -> return stringResource(AYMR.strings.exception_http, code)
            is UnknownHostException -> {
                return if (!isOnline()) {
                    stringResource(AYMR.strings.exception_offline)
                } else {
                    stringResource(AYMR.strings.exception_unknown_host, message ?: "")
                }
            }
            is NoChaptersException, is NoEpisodesException -> return stringResource(
                AYMR.strings.no_results_found,
            )
            is SourceNotInstalledException, is AnimeSourceNotInstalledException -> return stringResource(
                AYMR.strings.loader_not_implemented_error,
            )
        }
        return when (val className = this::class.simpleName) {
            "Exception", "IOException" -> message ?: className
            else -> "$className: $message"
        }
    }
