package pt.nunomatos.swordcats.presentation.ui.cats.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pt.nunomatos.swordcats.data.model.ApiResponseModel
import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.domain.use_case.UpdateFavoriteCatStateUseCase
import javax.inject.Inject

@HiltViewModel
class CatDetailsViewModel @Inject constructor(
    private val updateFavoriteCatStateUseCase: UpdateFavoriteCatStateUseCase
) : ViewModel() {

    private val catFlow: MutableSharedFlow<CatModel> = MutableSharedFlow()
    val readCatFlow: SharedFlow<CatModel> = catFlow

    private val stateFlow: MutableStateFlow<ApiResponseModel<*>> =
        MutableStateFlow(ApiResponseModel.Start)
    val readStateFlow: StateFlow<ApiResponseModel<*>> = stateFlow

    fun updateFavoriteCatState(cat: CatModel) {
        updateFavoriteCatStateUseCase.invoke(
            catId = cat.id,
            isFavorite = !cat.favoriteId.isNullOrBlank()
        )
            .onEach {
                stateFlow.value = it
                if (it.isSuccess()) {
                    it.data?.let { favoriteCat ->
                        catFlow.emit(
                            cat.copy(
                                favoriteId = favoriteCat.id
                            )
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}