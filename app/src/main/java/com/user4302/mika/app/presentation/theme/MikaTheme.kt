package com.user4302.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.user4302.domain.ui.UiPreferences
import com.user4302.domain.ui.model.AppTheme
import com.user4302.presentation.theme.colorscheme.BaseColorScheme
import com.user4302.presentation.theme.colorscheme.CloudflareColorScheme
import com.user4302.presentation.theme.colorscheme.CottoncandyColorScheme
import com.user4302.presentation.theme.colorscheme.DoomColorScheme
import com.user4302.presentation.theme.colorscheme.GreenAppleColorScheme
import com.user4302.presentation.theme.colorscheme.LavenderColorScheme
import com.user4302.presentation.theme.colorscheme.MatrixColorScheme
import com.user4302.presentation.theme.colorscheme.MidnightDuskColorScheme
import com.user4302.presentation.theme.colorscheme.MikaColorScheme
import com.user4302.presentation.theme.colorscheme.MochaColorScheme
import com.user4302.presentation.theme.colorscheme.MonetColorScheme
import com.user4302.presentation.theme.colorscheme.MonochromeColorScheme
import com.user4302.presentation.theme.colorscheme.NordColorScheme
import com.user4302.presentation.theme.colorscheme.SapphireColorScheme
import com.user4302.presentation.theme.colorscheme.StrawberryColorScheme
import com.user4302.presentation.theme.colorscheme.TakoColorScheme
import com.user4302.presentation.theme.colorscheme.TealTurqoiseColorScheme
import com.user4302.presentation.theme.colorscheme.TidalWaveColorScheme
import com.user4302.presentation.theme.colorscheme.YinYangColorScheme
import com.user4302.presentation.theme.colorscheme.YotsubaColorScheme
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Composable
fun MikaTheme(
    appTheme: AppTheme? = null,
    amoled: Boolean? = null,
    content: @Composable () -> Unit,
) {
    val uiPreferences = Injekt.get<UiPreferences>()
    BaseMikaTheme(
        appTheme = appTheme ?: uiPreferences.appTheme().get(),
        isAmoled = amoled ?: uiPreferences.themeDarkAmoled().get(),
        content = content,
    )
}

@Composable
fun MikaPreviewTheme(
    appTheme: AppTheme = AppTheme.DEFAULT,
    isAmoled: Boolean = false,
    content: @Composable () -> Unit,
) = BaseMikaTheme(appTheme, isAmoled, content)

@Composable
private fun BaseMikaTheme(
    appTheme: AppTheme,
    isAmoled: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = getThemeColorScheme(appTheme, isAmoled),
        content = content,
    )
}

@Composable
@ReadOnlyComposable
private fun getThemeColorScheme(
    appTheme: AppTheme,
    isAmoled: Boolean,
): ColorScheme {
    val uiPreferences = Injekt.get<UiPreferences>()
    val colorScheme = if (appTheme == AppTheme.MONET) {
        MonetColorScheme(LocalContext.current)
    } else {
        colorSchemes.getOrDefault(appTheme, MikaColorScheme)
    }
    return colorScheme.getColorScheme(
        isSystemInDarkTheme(),
        isAmoled,
    )
}

private const val RIPPLE_DRAGGED_ALPHA = .1f
private const val RIPPLE_FOCUSED_ALPHA = .1f
private const val RIPPLE_HOVERED_ALPHA = .1f
private const val RIPPLE_PRESSED_ALPHA = .1f

val playerRippleConfiguration
    @Composable get() = RippleConfiguration(
        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
        rippleAlpha = RippleAlpha(
            draggedAlpha = RIPPLE_DRAGGED_ALPHA,
            focusedAlpha = RIPPLE_FOCUSED_ALPHA,
            hoveredAlpha = RIPPLE_HOVERED_ALPHA,
            pressedAlpha = RIPPLE_PRESSED_ALPHA,
        ),
    )

private val colorSchemes: Map<AppTheme, BaseColorScheme> = mapOf(
    AppTheme.DEFAULT to MikaColorScheme,
    AppTheme.CLOUDFLARE to CloudflareColorScheme,
    AppTheme.COTTONCANDY to CottoncandyColorScheme,
    AppTheme.DOOM to DoomColorScheme,
    AppTheme.GREEN_APPLE to GreenAppleColorScheme,
    AppTheme.LAVENDER to LavenderColorScheme,
    AppTheme.MATRIX to MatrixColorScheme,
    AppTheme.MIDNIGHT_DUSK to MidnightDuskColorScheme,
    AppTheme.MONOCHROME to MonochromeColorScheme,
    AppTheme.MOCHA to MochaColorScheme,
    AppTheme.SAPPHIRE to SapphireColorScheme,
    AppTheme.NORD to NordColorScheme,
    AppTheme.STRAWBERRY_DAIQUIRI to StrawberryColorScheme,
    AppTheme.TAKO to TakoColorScheme,
    AppTheme.TEALTURQUOISE to TealTurqoiseColorScheme,
    AppTheme.TIDAL_WAVE to TidalWaveColorScheme,
    AppTheme.YINYANG to YinYangColorScheme,
    AppTheme.YOTSUBA to YotsubaColorScheme,
)
