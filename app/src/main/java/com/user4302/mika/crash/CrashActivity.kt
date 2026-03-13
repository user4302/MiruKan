package com.user4302.mika.crash

import android.content.Intent
import android.os.Bundle
import androidx.core.view.WindowCompat
import com.user4302.mika.ui.base.activity.BaseActivity
import com.user4302.mika.ui.main.MainActivity
import com.user4302.mika.util.view.setComposeContent
import com.user4302.presentation.crash.CrashScreen

class CrashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val exception = GlobalExceptionHandler.getThrowableFromIntent(intent)
        setComposeContent {
            CrashScreen(
                exception = exception,
                onRestartClick = {
                    finishAffinity()
                    startActivity(Intent(this@CrashActivity, MainActivity::class.java))
                },
            )
        }
    }
}
