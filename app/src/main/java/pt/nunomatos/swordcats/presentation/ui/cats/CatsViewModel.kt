package pt.nunomatos.swordcats.presentation.ui.cats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pt.nunomatos.swordcats.data.model.ApiResponseModel
import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.data.model.LoginState
import pt.nunomatos.swordcats.data.model.UserFeedModel
import pt.nunomatos.swordcats.domain.use_case.GetCatsUseCase
import pt.nunomatos.swordcats.domain.use_case.GetLoginStateUseCase
import pt.nunomatos.swordcats.domain.use_case.GetUserFeedUseCase
import pt.nunomatos.swordcats.domain.use_case.LogoutUseCase
import pt.nunomatos.swordcats.domain.use_case.UpdateFavoriteCatStateUseCase
import pt.nunomatos.swordcats.domain.use_case.UpdateUserFeedUseCase
import javax.inject.Inject

@HiltViewModel
class CatsViewModel @Inject constructor(
    getUserFeedUseCase: GetUserFeedUseCase,
    private val getCatsUseCase: GetCatsUseCase,
    getLoginStateUseCase: GetLoginStateUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val updateFavoriteCatStateUseCase: UpdateFavoriteCatStateUseCase,
    private val updateUserFeedUseCase: UpdateUserFeedUseCase
) : ViewModel() {

    private val searchQueryFlow: MutableStateFlow<String> = MutableStateFlow("")
    val readSearchQueryFlow: StateFlow<String> = searchQueryFlow

    private val filterFavoritesFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

//    private val loginStateFlow: MutableStateFlow<LoginState> = MutableStateFlow(LoginState.Unknown)
    val readLoginStateFlow: StateFlow<LoginState> = getLoginStateUseCase.invoke()

    private val currentSelectedTab: MutableStateFlow<Int> = MutableStateFlow(0)
    val readCurrentSelectedTab: StateFlow<Int> = currentSelectedTab

    private val userFeedFlow: MutableStateFlow<UserFeedModel?> = MutableStateFlow(null)
    val readUserFeedFlow: StateFlow<UserFeedModel?> = userFeedFlow

    private val mainStateFlow: MutableStateFlow<ApiResponseModel<*>> =
        MutableStateFlow(ApiResponseModel.Start)
    val readMainStateFlow: StateFlow<ApiResponseModel<*>> = mainStateFlow

    private val secondaryStateFlow: MutableStateFlow<ApiResponseModel<*>> =
        MutableStateFlow(ApiResponseModel.Start)
    val readSecondaryStateFlow: StateFlow<ApiResponseModel<*>> = secondaryStateFlow

    private val loadMoreCatsState: MutableStateFlow<ApiResponseModel<*>> =
        MutableStateFlow(ApiResponseModel.Start)
    val readLoadMoreCatsState: StateFlow<ApiResponseModel<*>> = loadMoreCatsState

    private val catsListFlow: MutableStateFlow<List<CatModel>> = MutableStateFlow(listOf())
    val readCatsListFlow: StateFlow<List<CatModel>> = catsListFlow

    private var page = 0

    init {
        getUserFeedUseCase.invoke()
            .onEach { userFeed ->
                userFeedFlow.value = userFeed
                page = userFeed?.feedPage ?: 0
                userFeed?.let { updateUserFeedUseCase.invoke(it) }
            }
            .launchIn(viewModelScope)

        getCats()

//        getLoginStateUseCase.invoke()
//            .onEach { loginStateFlow.value = it }
//            .launchIn(viewModelScope)

        viewModelScope.launch {
            combine(
                userFeedFlow.filterNotNull(),
                searchQueryFlow,
                filterFavoritesFlow,
            ) { feed, query, filterFavorites ->
                feed.cats
                    .filter {
                        !filterFavorites || !it.favoriteId.isNullOrBlank()
                    }
                    .filter {
                        query.isEmpty() || it.breeds.firstOrNull()?.name?.lowercase()
                            ?.contains(query.lowercase()) == true
                    }
            }.collect { filteredList ->
                catsListFlow.emit(filteredList)
            }
        }
    }

    fun getCats() {
        getCatsUseCase.invoke(page = page)
            .onEach {
                if (page != 0) {
                    loadMoreCatsState.value = it
                } else {
                    mainStateFlow.value = it
                }

                if (it.isSuccess()) {
                    page++
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateSearchQueryValue(query: String) {
        searchQueryFlow.value = query
    }

    fun updateFavoriteCatState(id: String, isFavorite: Boolean) {
        updateFavoriteCatStateUseCase.invoke(id, isFavorite)
            .onEach { secondaryStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun updateSelectedTab(index: Int, filterFavorites: Boolean) {
        currentSelectedTab.value = index
        filterFavoritesFlow.value = filterFavorites
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase.invoke()
            page = 0
        }
    }
}