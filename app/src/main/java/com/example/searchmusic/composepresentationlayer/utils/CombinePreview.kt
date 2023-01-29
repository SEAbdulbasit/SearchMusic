
package com.example.searchmusic.composepresentationlayer.utils

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@ThemeOptionPreview
annotation class CombinedPreviews


@DarkThemePreview
annotation class ThemeOptionPreview

@Preview(
    name = "Dark Theme",
    group = "Dark Theme",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
annotation class DarkThemePreview