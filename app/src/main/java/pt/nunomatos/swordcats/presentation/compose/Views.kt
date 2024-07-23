package pt.nunomatos.swordcats.presentation.compose

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SwordCatsInputField(
    modifier: Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    @StringRes hintRes: Int
) {
    TextField(
        modifier = modifier
            .border(
                width = 1.dp,
                color = Purple,
                shape = RoundedCornerShape(size = 16.dp)
            ),
        value = value,
        onValueChange = {
            onValueChanged(it)
        },
        placeholder = {
            Text(
                text = stringResource(id = hintRes),
                style = TextStyle(
                    color = LightGray,
                    fontSize = 14.sp,
                    fontFamily = Raleway,
                    fontWeight = FontWeight.Medium
                )
            )
        },
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 14.sp,
            fontFamily = Raleway,
            fontWeight = FontWeight.Medium
        ),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            cursorColor = LightGray,
            focusedIndicatorColor = Color.Transparent,
        )
    )
}

@Composable
fun SwordCatsButton(
    modifier: Modifier,
    enabled: Boolean = true,
    @StringRes textRes: Int,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Purple,
            disabledBackgroundColor = DarkGray,
        ),
        onClick = {
            onClick()
        },
        content = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                text = stringResource(id = textRes),
                style = TextStyle(
                    fontFamily = Raleway,
                    fontSize = 16.sp,
                    color = if (enabled) {
                        Color.White
                    } else {
                        Color.Gray
                    },
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            )
        }
    )
}

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center,
        content = {
            LinearProgressIndicator(
                color = Purple
            )
        }
    )
}

@Composable
fun BottomSnackbar(show: Boolean, message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (show) {
                    Color.Black.copy(alpha = 0.5f)
                } else {
                    Color.Transparent
                }
            ),
        contentAlignment = Alignment.BottomCenter,
        content = {
            AnimatedVisibility(
                visible = show,
                enter = slideInVertically(
                    initialOffsetY = {
                        it
                    },
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = LinearEasing
                    )
                ),
                exit = slideOutVertically(
                    targetOffsetY = {
                        it
                    },
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = LinearEasing
                    )
                ),
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(enabled = false, onClick = {}),
                        content = {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(color = DarkGray)
                                    .windowInsetsPadding(
                                        WindowInsets.navigationBars.only(
                                            WindowInsetsSides.Bottom
                                        )
                                    )
                                    .padding(
                                        start = 24.dp,
                                        end = 24.dp,
                                        top = 24.dp,
                                        bottom = 48.dp
                                    ),
                                text = message,
                                style = TextStyle(
                                    fontFamily = Raleway,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            )
                        }
                    )
                }
            )
        }
    )
}