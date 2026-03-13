package com.user4302.mika.ui.setting

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.user4302.mika.presentation.core.components.TwoPanelBox
import com.user4302.presentation.more.settings.screen.player.PlayerSettingsMainScreen
import com.user4302.presentation.more.settings.screen.player.PlayerSettingsPlayerScreen
import com.user4302.presentation.util.DefaultNavigatorScreenTransition
import com.user4302.presentation.util.LocalBackPress
import com.user4302.presentation.util.Screen
import com.user4302.presentation.util.isTabletUi

class PlayerSettingsScreen(private val mainSettings: Boolean) : Screen() {
    @Composable
    override fun Content() {
        val parentNavigator = LocalNavigator.currentOrThrow
        if (!isTabletUi()) {
            Navigator(
                screen = PlayerSettingsMainScreen(mainSettings),
                content = {
                    val pop: () -> Unit = {
                        if (it.canPop) {
                            it.pop()
                        } else {
                            parentNavigator.pop()
                        }
                    }
                    CompositionLocalProvider(LocalBackPress provides pop) {
                        DefaultNavigatorScreenTransition(navigator = it)
                    }
                },
            )
        } else {
            Navigator(
                screen = PlayerSettingsPlayerScreen,
            ) {
                val insets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
                TwoPanelBox(
                    modifier = Modifier
                        .windowInsetsPadding(insets)
                        .consumeWindowInsets(insets),
                    startContent = {
                        CompositionLocalProvider(LocalBackPress provides parentNavigator::pop) {
                            PlayerSettingsMainScreen(mainSettings).Content(twoPane = true)
                        }
                    },
                    endContent = { DefaultNavigatorScreenTransition(navigator = it) },
                )
            }
        }
    }
}
