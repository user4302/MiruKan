package com.user4302.mika.ui.setting.track

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.user4302.mika.data.track.TrackerManager
import com.user4302.mika.presentation.core.screens.LoadingScreen
import com.user4302.mika.ui.base.activity.BaseActivity
import com.user4302.mika.ui.main.MainActivity
import com.user4302.mika.util.view.setComposeContent
import uy.kohesive.injekt.injectLazy

abstract class BaseOAuthLoginActivity : BaseActivity() {

    internal val trackerManager: TrackerManager by injectLazy()

    abstract fun handleResult(data: Uri?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setComposeContent {
            LoadingScreen()
        }

        handleResult(intent.data)
    }

    internal fun returnToSettings() {
        finish()

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
    }
}
