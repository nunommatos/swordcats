package pt.nunomatos.swordcats.presentation.ui.cats.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.delay
import pt.nunomatos.swordcats.R
import pt.nunomatos.swordcats.common.Constants
import pt.nunomatos.swordcats.data.model.ApiResponseModel
import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.presentation.compose.AppBackground
import pt.nunomatos.swordcats.presentation.compose.BottomSnackbar
import pt.nunomatos.swordcats.presentation.compose.LightGray
import pt.nunomatos.swordcats.presentation.compose.LoadingOverlay
import pt.nunomatos.swordcats.presentation.compose.Purple
import pt.nunomatos.swordcats.presentation.compose.Raleway
import pt.nunomatos.swordcats.common.openUrl

@Composable
fun CatDetailsScreen(cat: CatModel, viewModel: CatDetailsViewModel, navController: NavController) {
    val currentCat = remember { mutableStateOf(cat) }
    var state by remember { mutableStateOf<ApiResponseModel<*>?>(null) }

    LaunchedEffect(Unit) {
        viewModel.readCatFlow.collect {
            currentCat.value = it
        }
    }

    LaunchedEffect(Unit) {
        viewModel.readStateFlow.collect { newState ->
            state = newState
            if (newState.isError()) {
                delay(Constants.Animation.DURATION_SNACKBAR)
                state = null
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(all = 16.dp),
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = AppBackground)
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    Image(
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        },
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .weight(1f),
                        text = cat.breeds.first().name,
                        style = TextStyle(
                            color = Color.LightGray,
                            fontSize = 24.sp,
                            fontFamily = Raleway,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                viewModel.updateFavoriteCatState(
                                    cat = currentCat.value
                                )
                            },
                        painter = painterResource(
                            id = if (!currentCat.value.favoriteId.isNullOrBlank()) {
                                R.drawable.ic_favorite_on
                            } else {
                                R.drawable.ic_favorite_off
                            }
                        ), contentDescription = null
                    )
                }
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                content = {
                    Column(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .verticalScroll(rememberScrollState()),
                        content = {
                            AsyncImage(
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = Purple,
                                        shape = RoundedCornerShape(size = 12.dp)
                                    )
                                    .aspectRatio(4 / 3f)
                                    .clip(RoundedCornerShape(size = 12.dp)),
                                contentScale = ContentScale.Crop,
                                model = cat.image,
                                contentDescription = null
                            )

                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                crossAxisSpacing = 12.dp,
                                crossAxisAlignment = FlowCrossAxisAlignment.Center,
                                content = {
                                    CatDetailDescriptionItem(
                                        showImage = false,
                                        text = currentCat.value.getOrigin()
                                    )
                                    currentCat.value.getAverageLifeSpanText()
                                        .takeIf { it.isNotBlank() }
                                        ?.let {
                                            CatDetailDescriptionItem(
                                                showImage = false,
                                                text = stringResource(
                                                    id = R.string.cat_details_screen_average_lifespan,
                                                    it
                                                )
                                            )
                                        }
                                    currentCat.value.getAverageWeightText()
                                        .takeIf { it.isNotBlank() }
                                        ?.let {
                                            CatDetailDescriptionItem(
                                                showImage = false,
                                                text = stringResource(
                                                    id = R.string.cat_details_screen_average_weight,
                                                    it
                                                )
                                            )
                                        }
                                    currentCat.value.getTemperamentItems()
                                        .forEach { temperamentItem ->
                                            CatDetailDescriptionItem(
                                                showImage = true,
                                                text = temperamentItem
                                            )
                                        }
                                }
                            )

                            Text(
                                modifier = Modifier.padding(top = 24.dp),
                                text = currentCat.value.getDescription(),
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontFamily = Raleway,
                                    fontWeight = FontWeight.Medium
                                )
                            )

                            Spacer(modifier = Modifier.height(96.dp))
                        }
                    )
                    if (!currentCat.value.getDetailsUrl().isNullOrBlank()) {
                        val context = LocalContext.current
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            AppBackground,
                                            AppBackground
                                        )
                                    )
                                )
                                .align(Alignment.BottomCenter)
                                .windowInsetsPadding(
                                    WindowInsets.navigationBars.only(
                                        WindowInsetsSides.Bottom
                                    )
                                ),
                            contentAlignment = Alignment.BottomCenter,
                            content = {
                                Button(
                                    colors = ButtonDefaults
                                        .buttonColors(
                                            backgroundColor = Purple
                                        ),
                                    onClick = {
                                        context.openUrl(currentCat.value.getDetailsUrl().orEmpty())
                                    },
                                    content = {
                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(all = 8.dp),
                                            text = stringResource(id = R.string.cat_details_screen_learn_more),
                                            style = TextStyle(
                                                fontFamily = Raleway,
                                                fontSize = 16.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Medium,
                                                textAlign = TextAlign.Center
                                            )
                                        )
                                    }
                                )
                            }
                        )
                    }
                }
            )
        }
    )

    if (state?.isLoading() == true) {
        LoadingOverlay()
    }

    BottomSnackbar(
        message = stringResource(
            id = if (state?.isGenericError() == true) {
                R.string.snackbar_message_error_generic
            } else {
                R.string.snackbar_message_error_no_internet
            }
        ),
        show = state?.isError() == true
    )
}

@Composable
private fun CatDetailDescriptionItem(showImage: Boolean, text: String) {
    Row(
        modifier = Modifier
            .padding(end = 12.dp)
            .background(
                color = LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            if (showImage) {
                Image(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(16.dp),
                    painter = painterResource(id = R.drawable.ic_paw),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        color = AppBackground
                    )
                )
            }
            Text(
                text = text,
                style = TextStyle(
                    color = AppBackground,
                    fontSize = 14.sp,
                    fontFamily = Raleway,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    )
}