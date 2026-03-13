package com.user4302.presentation.reader.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.user4302.mika.presentation.core.components.CheckboxItem
import com.user4302.mika.presentation.core.components.SettingsChipRow
import com.user4302.mika.presentation.core.components.SliderItem
import com.user4302.mika.presentation.core.i18n.pluralStringResource
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.presentation.core.util.collectAsState
import com.user4302.mika.ui.reader.setting.ReaderPreferences
import com.user4302.mika.ui.reader.setting.ReaderSettingsScreenModel

private val themes = listOf(
    AYMR.strings.black_background to 1,
    AYMR.strings.gray_background to 2,
    AYMR.strings.white_background to 0,
    AYMR.strings.automatic_background to 3,
)

private val flashColors = listOf(
    AYMR.strings.pref_flash_style_black to ReaderPreferences.FlashColor.BLACK,
    AYMR.strings.pref_flash_style_white to ReaderPreferences.FlashColor.WHITE,
    AYMR.strings.pref_flash_style_white_black to ReaderPreferences.FlashColor.WHITE_BLACK,
)

@Composable
internal fun ColumnScope.GeneralPage(screenModel: ReaderSettingsScreenModel) {
    val readerTheme by screenModel.preferences.readerTheme().collectAsState()

    val flashPageState by screenModel.preferences.flashOnPageChange().collectAsState()

    val flashMillisPref = screenModel.preferences.flashDurationMillis()
    val flashMillis by flashMillisPref.collectAsState()

    val flashIntervalPref = screenModel.preferences.flashPageInterval()
    val flashInterval by flashIntervalPref.collectAsState()

    val flashColorPref = screenModel.preferences.flashColor()
    val flashColor by flashColorPref.collectAsState()

    SettingsChipRow(AYMR.strings.pref_reader_theme) {
        themes.map { (labelRes, value) ->
            FilterChip(
                selected = readerTheme == value,
                onClick = { screenModel.preferences.readerTheme().set(value) },
                label = { Text(stringResource(labelRes)) },
            )
        }
    }

    CheckboxItem(
        label = stringResource(AYMR.strings.pref_show_page_number),
        pref = screenModel.preferences.showPageNumber(),
    )

    CheckboxItem(
        label = stringResource(AYMR.strings.pref_fullscreen),
        pref = screenModel.preferences.fullscreen(),
    )

    if (screenModel.hasDisplayCutout && screenModel.preferences.fullscreen().get()) {
        CheckboxItem(
            label = stringResource(AYMR.strings.pref_cutout_short),
            pref = screenModel.preferences.cutoutShort(),
        )
    }

    CheckboxItem(
        label = stringResource(AYMR.strings.pref_keep_screen_on),
        pref = screenModel.preferences.keepScreenOn(),
    )

    CheckboxItem(
        label = stringResource(AYMR.strings.pref_read_with_long_tap),
        pref = screenModel.preferences.readWithLongTap(),
    )

    CheckboxItem(
        label = stringResource(AYMR.strings.pref_always_show_chapter_transition),
        pref = screenModel.preferences.alwaysShowChapterTransition(),
    )

    CheckboxItem(
        label = stringResource(AYMR.strings.pref_page_transitions),
        pref = screenModel.preferences.pageTransitions(),
    )

    CheckboxItem(
        label = stringResource(AYMR.strings.pref_flash_page),
        pref = screenModel.preferences.flashOnPageChange(),
    )
    if (flashPageState) {
        SliderItem(
            value = flashMillis / ReaderPreferences.MILLI_CONVERSION,
            valueRange = 1..15,
            label = stringResource(AYMR.strings.pref_flash_duration),
            valueText = stringResource(AYMR.strings.pref_flash_duration_summary, flashMillis),
            onChange = { flashMillisPref.set(it * ReaderPreferences.MILLI_CONVERSION) },
            pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
        SliderItem(
            value = flashInterval,
            valueRange = 1..10,
            label = stringResource(AYMR.strings.pref_flash_page_interval),
            valueText = pluralStringResource(AYMR.plurals.pref_pages, flashInterval, flashInterval),
            onChange = {
                flashIntervalPref.set(it)
            },
            pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
        SettingsChipRow(AYMR.strings.pref_flash_with) {
            flashColors.map { (labelRes, value) ->
                FilterChip(
                    selected = flashColor == value,
                    onClick = { flashColorPref.set(value) },
                    label = { Text(stringResource(labelRes)) },
                )
            }
        }
    }
}
