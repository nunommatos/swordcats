package pt.nunomatos.swordcats.presentation.ui.cats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pt.nunomatos.swordcats.data.model.ApiResponse
import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.data.model.UserModel
import pt.nunomatos.swordcats.domain.model.LocalFeedMessage
import pt.nunomatos.swordcats.domain.use_case.GetCatsUseCase
import pt.nunomatos.swordcats.domain.use_case.GetLocalFeedUseCase
import pt.nunomatos.swordcats.domain.use_case.GetLoggedUserUseCase
import pt.nunomatos.swordcats.domain.use_case.LogoutUseCase
import pt.nunomatos.swordcats.domain.use_case.UpdateFavoriteCatStateUseCase
import javax.inject.Inject

@HiltViewModel
class CatsViewModel @Inject constructor(
    private val getCatsUseCase: GetCatsUseCase,
    getLocalFeedUseCase: GetLocalFeedUseCase,
    getLoggedUserUseCase: GetLoggedUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val updateFavoriteCatStateUseCase: UpdateFavoriteCatStateUseCase
) : ViewModel() {

    private val loggedUserFlow: MutableStateFlow<UserModel> = MutableStateFlow(UserModel())
    val readLoggedUserFlow = loggedUserFlow.asStateFlow()

    private val catsListFlow: MutableStateFlow<List<CatModel>> = MutableStateFlow(listOf())
    val readCatsListFlow = catsListFlow.asStateFlow()

    private val logoutFlow: MutableSharedFlow<Unit> = MutableSharedFlow()
    val readLogoutFlow = logoutFlow.asSharedFlow()

    private val loadMoreCatsState: MutableStateFlow<ApiResponse<*>> =
        MutableStateFlow(ApiResponse.Start)
    val readLoadMoreCatsState = loadMoreCatsState.asStateFlow()

    private val mainStateFlow: MutableStateFlow<ApiResponse<*>> =
        MutableStateFlow(ApiResponse.Start)
    val readMainStateFlow = mainStateFlow.asStateFlow()

    private val secondaryStateFlow: MutableStateFlow<ApiResponse<*>> =
        MutableStateFlow(ApiResponse.Start)
    val readSecondaryStateFlow = secondaryStateFlow.asStateFlow()

    private val localFeedMessage: MutableStateFlow<LocalFeedMessage> =
        MutableStateFlow(LocalFeedMessage())
    val readLocalFeedMessage = localFeedMessage.asStateFlow()

    private val searchQueryFlow: MutableStateFlow<String> = MutableStateFlow("")
    val readSearchQueryFlow = searchQueryFlow.asStateFlow()

    private val filterFavoritesFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val currentSelectedTab: MutableStateFlow<Int> = MutableStateFlow(0)
    val readCurrentSelectedTab = currentSelectedTab.asStateFlow()

    init {
        viewModelScope.launch {
            getLoggedUserUseCase.invoke().collect { user ->
                if (user != null) {
                    loggedUserFlow.emit(user)
                } else {
                    logoutFlow.emit(Unit)
                }
            }
        }

        getCats()

        viewModelScope.launch {
            getLocalFeedUseCase.invoke().collect { localFeed ->
                localFeedMessage.emit(
                    LocalFeedMessage(
                        show = true,
                        updatedAt = localFeed.getUpdatedDate()
                    )
                )
            }
        }

        listenToFeedChanges()
    }

    private fun getCats(initialRequest: Boolean, flowToUpdate: MutableStateFlow<ApiResponse<*>>) {
        viewModelScope.launch {
            getCatsUseCase.invoke(initialRequest).collect {
                flowToUpdate.emit(it)
            }
        }
    }

    private fun listenToFeedChanges() {
        viewModelScope.launch {
            combine(
                loggedUserFlow.map { user -> user.userFeed }.filterNotNull(),
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
        getCats(
            initialRequest = true,
            flowToUpdate = mainStateFlow
        )
    }

    fun getMoreCats() {
        getCats(
            initialRequest = false,
            flowToUpdate = loadMoreCatsState
        )
    }

    fun updateSelectedTab(index: Int, filterFavorites: Boolean) {
        currentSelectedTab.value = index
        filterFavoritesFlow.value = filterFavorites
    }

    fun updateSearchQueryValue(query: String) {
        searchQueryFlow.value = query
    }

    fun updateFavoriteCatState(id: String, isFavorite: Boolean) {
        viewModelScope.launch {
            updateFavoriteCatStateUseCase.invoke(id, isFavorite).collect {
                secondaryStateFlow.emit(it)
            }
        }
    }

    fun dismissError() {
        secondaryStateFlow.value = ApiResponse.Start
    }

    fun dismissLoadMoreCatsError() {
        loadMoreCatsState.value = ApiResponse.Start
    }

    fun dismissLocalFeedMessage() {
        localFeedMessage.value = LocalFeedMessage()
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase.invoke()
        }
    }
}