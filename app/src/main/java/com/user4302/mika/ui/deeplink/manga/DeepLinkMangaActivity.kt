package com.user4302.mika.ui.deeplink.manga

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.user4302.mika.ui.deeplink.DeepLinkScreenType
import com.user4302.mika.ui.main.MainActivity

class DeepLinkMangaActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.apply {
            flags = flags or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(MainActivity.INTENT_SEARCH_TYPE, DeepLinkScreenType.MANGA.toString())
            setClass(applicationContext, MainActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
