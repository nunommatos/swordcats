package pt.nunomatos.swordcats.presentation.compose

import android.app.Activity
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun CatsAppTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    SideEffect {
        with(view.context as Activity) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = AppBackground.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
        }
    }
    MaterialTheme(
        content = content
    )
}