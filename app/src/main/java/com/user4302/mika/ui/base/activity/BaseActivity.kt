package com.user4302.mika.ui.base.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.user4302.mika.ui.base.delegate.SecureActivityDelegate
import com.user4302.mika.ui.base.delegate.SecureActivityDelegateImpl
import com.user4302.mika.ui.base.delegate.ThemingDelegate
import com.user4302.mika.ui.base.delegate.ThemingDelegateImpl
import com.user4302.mika.util.system.prepareTabletUiContext

open class BaseActivity :
    AppCompatActivity(),
    SecureActivityDelegate by SecureActivityDelegateImpl(),
    ThemingDelegate by ThemingDelegateImpl() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.prepareTabletUiContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyAppTheme(this)
        super.onCreate(savedInstanceState)
    }
}
