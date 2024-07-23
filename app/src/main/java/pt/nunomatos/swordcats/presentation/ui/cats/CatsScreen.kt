package pt.nunomatos.swordcats.presentation.ui.cats

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.delay
import pt.nunomatos.swordcats.R
import pt.nunomatos.swordcats.common.Constants
import pt.nunomatos.swordcats.data.model.ARGUMENT_CAT_DETAILS_VALUE
import pt.nunomatos.swordcats.data.model.ApiResponseModel
import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.data.model.CatsRoute
import pt.nunomatos.swordcats.data.model.UserFeedModel
import pt.nunomatos.swordcats.presentation.compose.AppBackground
import pt.nunomatos.swordcats.presentation.compose.BottomSnackbar
import pt.nunomatos.swordcats.presentation.compose.DarkGray
import pt.nunomatos.swordcats.presentation.compose.LightGray
import pt.nunomatos.swordcats.presentation.compose.LightGray2
import pt.nunomatos.swordcats.presentation.compose.LoadingOverlay
import pt.nunomatos.swordcats.presentation.compose.Purple
import pt.nunomatos.swordcats.presentation.compose.Raleway
import pt.nunomatos.swordcats.presentation.compose.SwordCatsInputField

private const val TAB_ALL = 0
private const val TAB_FAVORITES = 1

@Composable
fun CatsScreen(
    viewModel: CatsViewModel,
    navController: NavController,
) {
    val mainState by viewModel.readMainStateFlow.collectAsState()
    val catsList by viewModel.readCatsListFlow.collectAsState()
    val searchQueryValue by viewModel.readSearchQueryFlow.collectAsState()
    val tabIndex by viewModel.readCurrentSelectedTab.collectAsState()
    val catsListState = rememberLazyListState()
    var userName by remember { mutableStateOf("") }
    var userFeed by remember { mutableStateOf<UserFeedModel?>(null) }
    var showNetworkSnackbar by remember { mutableStateOf(false) }
    var secondaryState by remember { mutableStateOf<ApiResponseModel<*>?>(null) }
    var loadMoreCatsState by remember { mutableStateOf<ApiResponseModel<*>?>(null) }
    val isOnCatsListBottom by remember {
        derivedStateOf {
            catsListState.layoutInfo.visibleItemsInfo.lastOrNull()?.let { lastVisibleItem ->
                lastVisibleItem.index != 0 &&
                        lastVisibleItem.index == catsListState.layoutInfo.totalItemsCount - 1
            } ?: false
        }
    }

    LaunchedEffect(Unit) {
        userFeed = viewModel.readUserFeedFlow.value
        if (userFeed?.isLocalFeed == true) {
            showNetworkSnackbar = true
            delay(Constants.Animation.DURATION_SNACKBAR)
            showNetworkSnackbar = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.readSecondaryStateFlow.collect { state ->
            secondaryState = state
            if (state.isError()) {
                delay(Constants.Animation.DURATION_SNACKBAR)
                secondaryState = null
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.readLoginStateFlow.collect { loginState ->
            if (loginState.isLoggedIn()) {
                userName = loginState.user?.name.orEmpty()
            } else {
                navController.navigate(CatsRoute.Login.name) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.readLoadMoreCatsState.collect { state ->
            loadMoreCatsState = state
            if (state.isError()) {
                delay(Constants.Animation.DURATION_SNACKBAR)
                loadMoreCatsState = null
            }
        }
    }

    if (isOnCatsListBottom && loadMoreCatsState?.isLoading() != true) {
        viewModel.getCats()
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_paw),
                                colorFilter = ColorFilter.tint(
                                    color = LightGray
                                ),
                                contentDescription = null
                            )
                            Text(
                                modifier = Modifier.padding(top = 8.dp),
                                text = stringResource(id = R.string.app_name),
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontFamily = Raleway,
                                    fontWeight = FontWeight.Light
                                )
                            )
                        }
                    )
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .weight(1f),
                        text = "Hello, $userName",
                        style = TextStyle(
                            color = Color.LightGray,
                            fontSize = 18.sp,
                            fontFamily = Raleway,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Image(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                viewModel.logout()
                            },
                        painter = painterResource(id = R.drawable.ic_logout),
                        contentDescription = null
                    )
                }
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
                content = {
                    if (mainState.isSuccess()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            content = {
                                SwordCatsInputField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    value = searchQueryValue,
                                    onValueChanged = {
                                        viewModel.updateSearchQueryValue(it)
                                    },
                                    hintRes = R.string.input_hint_search_breed
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    content = {
                                        CatsTab(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 8.dp),
                                            textRes = R.string.cats_screen_tab_all,
                                            isSelected = tabIndex == TAB_ALL,
                                            onTabSelected = {
                                                viewModel.updateSelectedTab(
                                                    index = TAB_ALL,
                                                    filterFavorites = false
                                                )
                                            }
                                        )
                                        CatsTab(
                                            modifier = Modifier.weight(1f),
                                            textRes = R.string.cats_screen_tab_favorites,
                                            isSelected = tabIndex == TAB_FAVORITES,
                                            onTabSelected = {
                                                viewModel.updateSelectedTab(
                                                    index = TAB_FAVORITES,
                                                    filterFavorites = true
                                                )
                                            }
                                        )
                                    }
                                )

                                if (catsList.isNotEmpty()) {
                                    CatsListView(
                                        lazyState = catsListState,
                                        showLoading = loadMoreCatsState?.isLoading() == true,
                                        catsList = catsList,
                                        onCatClicked = {
                                            navController.navigate(
                                                CatsRoute.CatDetails.name.replace(
                                                    ARGUMENT_CAT_DETAILS_VALUE,
                                                    Gson().toJson(it)
                                                )
                                            )
                                        },
                                        onCatFavoriteClicked = { catId, isFavorite ->
                                            viewModel.updateFavoriteCatState(
                                                id = catId,
                                                isFavorite = isFavorite
                                            )
                                        }
                                    )
                                } else {
                                    ErrorView(
                                        imageRes = R.drawable.ic_cat,
                                        messageRes = if (searchQueryValue.isNotBlank()) {
                                            R.string.empty_message_no_results_filtered
                                        } else if (tabIndex == TAB_FAVORITES) {
                                            R.string.empty_message_no_favorites
                                        } else {
                                            R.string.empty_message_no_results
                                        }
                                    )
                                }
                            }
                        )
                    } else if (mainState.isLoading()) {
                        CircularProgressIndicator(
                            color = LightGray
                        )
                    } else if (mainState.isGenericError()) {
                        ErrorView(
                            imageRes = R.drawable.ic_error,
                            messageRes = R.string.error_message_generic,
                            clickable = true,
                            onClick = {
                                viewModel.getCats()
                            }
                        )
                    } else if (mainState.isNetworkError()) {
                        ErrorView(
                            imageRes = R.drawable.ic_error,
                            messageRes = R.string.error_message_no_internet,
                            clickable = true,
                            onClick = {
                                viewModel.getCats()
                            }
                        )
                    }
                }
            )
        }
    )

    BottomSnackbar(
        message = stringResource(
            id = R.string.cats_screen_outdated_information,
            userFeed?.getUpdatedDate().orEmpty()
        ),
        show = showNetworkSnackbar
    )

    if (secondaryState?.isLoading() == true) {
        LoadingOverlay()
    }

    BottomSnackbar(
        message = stringResource(
            id = if (secondaryState?.isGenericError() == true) {
                R.string.snackbar_message_error_generic
            } else {
                R.string.snackbar_message_error_no_internet
            }
        ),
        show = secondaryState?.isError() == true
    )

    BottomSnackbar(
        message = stringResource(id = R.string.error_message_loading_more_cats),
        show = loadMoreCatsState?.isError() == true
    )
}

@Composable
private fun CatsTab(
    modifier: Modifier,
    @StringRes textRes: Int,
    isSelected: Boolean,
    onTabSelected: () -> Unit
) {
    Text(
        modifier = modifier
            .background(
                color = if (isSelected) {
                    Purple
                } else {
                    DarkGray
                },
                shape = RoundedCornerShape(32.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onTabSelected() }
            )
            .padding(12.dp),
        text = stringResource(id = textRes),
        style = TextStyle(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            fontFamily = Raleway,
            fontSize = 16.sp,
            color = if (isSelected) {
                Color.White
            } else {
                LightGray
            }
        ),
    )
}

@Composable
private fun CatsListView(
    lazyState: LazyListState,
    showLoading: Boolean,
    catsList: List<CatModel>,
    onCatClicked: (CatModel) -> Unit,
    onCatFavoriteClicked: (String, Boolean) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(vertical = 16.dp),
        state = lazyState,
        content = {
            items(
                items = catsList,
                key = { index ->
                    index.hashCode()
                }
            ) { cat ->
                val isFavorite = !cat.favoriteId.isNullOrBlank()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCatClicked(cat) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Image(
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    onCatFavoriteClicked(cat.id, isFavorite)
                                },
                            painter = painterResource(
                                id = if (isFavorite) {
                                    R.drawable.ic_favorite_on
                                } else {
                                    R.drawable.ic_favorite_off
                                }
                            ), contentDescription = null
                        )
                        AsyncImage(
                            model = cat.image,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .clip(CircleShape)
                                .size(48.dp)
                                .border(
                                    width = 1.dp,
                                    color = Purple,
                                    shape = CircleShape
                                ),
                            contentScale = ContentScale.Crop,
                            contentDescription = null
                        )
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .weight(1f),
                            verticalArrangement = Arrangement.Center,
                            content = {
                                Text(
                                    text = cat.breeds.first().name,
                                    style = TextStyle(
                                        fontFamily = Raleway,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                )
                                if (isFavorite) {
                                    val lifeSpan =
                                        cat.getAverageLifeSpan()
                                    Text(
                                        modifier = Modifier.padding(
                                            top = 2.dp
                                        ),
                                        text = stringResource(
                                            id = R.string.cats_screen_item_average_lifespan,
                                            lifeSpan.first,
                                            lifeSpan.second
                                        ),
                                        style = TextStyle(
                                            fontFamily = Raleway,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = LightGray2
                                        )
                                    )
                                }
                            }
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_right),
                            contentDescription = null
                        )
                    }
                )
            }
            item {
                if (showLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                        content = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = LightGray
                            )
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun ErrorView(
    @DrawableRes imageRes: Int,
    @StringRes messageRes: Int,
    clickable: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                enabled = clickable,
                onClick = {
                    onClick()
                }
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            Image(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = imageRes),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = LightGray)
            )
            Text(
                modifier = Modifier.padding(all = 24.dp),
                text = stringResource(id = messageRes),
                style = TextStyle(
                    fontFamily = Raleway,
                    fontSize = 16.sp,
                    color = LightGray,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    )
}
