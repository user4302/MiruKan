package com.user4302.mika.ui.base.delegate

import android.app.Activity
import com.user4302.domain.ui.UiPreferences
import com.user4302.domain.ui.model.AppTheme
import com.user4302.mika.R
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

interface ThemingDelegate {
    fun applyAppTheme(activity: Activity)

    companion object {
        fun getThemeResIds(appTheme: AppTheme, isAmoled: Boolean): List<Int> {
            return buildList(2) {
                add(themeResources.getOrDefault(appTheme, R.style.Theme_Mika))
                if (isAmoled) add(R.style.ThemeOverlay_Mika_Amoled)
            }
        }
    }
}

class ThemingDelegateImpl : ThemingDelegate {
    override fun applyAppTheme(activity: Activity) {
        val uiPreferences = Injekt.get<UiPreferences>()
        ThemingDelegate.getThemeResIds(
            uiPreferences.appTheme().get(),
            uiPreferences.themeDarkAmoled().get(),
        )
            .forEach(activity::setTheme)
    }
}

private val themeResources: Map<AppTheme, Int> = mapOf(
    AppTheme.MONET to R.style.Theme_Mika_Monet,
    AppTheme.COTTONCANDY to R.style.Theme_Mika_CottonCandy,
    AppTheme.GREEN_APPLE to R.style.Theme_Mika_GreenApple,
    AppTheme.LAVENDER to R.style.Theme_Mika_Lavender,
    AppTheme.MIDNIGHT_DUSK to R.style.Theme_Mika_MidnightDusk,
    AppTheme.MONOCHROME to R.style.Theme_Mika_Monochrome,
    AppTheme.MOCHA to R.style.Theme_Mika_Mocha,
    AppTheme.NORD to R.style.Theme_Mika_Nord,
    AppTheme.STRAWBERRY_DAIQUIRI to R.style.Theme_Mika_StrawberryDaiquiri,
    AppTheme.TAKO to R.style.Theme_Mika_Tako,
    AppTheme.TEALTURQUOISE to R.style.Theme_Mika_TealTurquoise,
    AppTheme.YINYANG to R.style.Theme_Mika_YinYang,
    AppTheme.YOTSUBA to R.style.Theme_Mika_Yotsuba,
    AppTheme.CLOUDFLARE to R.style.Theme_Mika_Cloudflare,
    AppTheme.SAPPHIRE to R.style.Theme_Mika_Sapphire,
    AppTheme.DOOM to R.style.Theme_Mika_Doom,
    AppTheme.MATRIX to R.style.Theme_Mika_Matrix,
    AppTheme.TIDAL_WAVE to R.style.Theme_Mika_TidalWave,
)
