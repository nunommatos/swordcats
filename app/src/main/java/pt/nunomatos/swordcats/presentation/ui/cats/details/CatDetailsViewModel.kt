package pt.nunomatos.swordcats.presentation.ui.cats.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.nunomatos.swordcats.common.Constants.Keys.ARGUMENT_CAT_DETAILS_KEY
import pt.nunomatos.swordcats.data.model.ApiResponse
import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.domain.use_case.UpdateFavoriteCatStateUseCase
import javax.inject.Inject

@HiltViewModel
class CatDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val updateFavoriteCatStateUseCase: UpdateFavoriteCatStateUseCase
) : ViewModel() {

    private val catFlow: MutableStateFlow<CatModel> = MutableStateFlow(getCatFromArguments())
    val readCatFlow = catFlow.asStateFlow()

    private val stateFlow: MutableStateFlow<ApiResponse<*>> =
        MutableStateFlow(ApiResponse.Start)
    val readStateFlow = stateFlow.asStateFlow()

    private fun getCatFromArguments(): CatModel {
        val catStr = savedStateHandle.get<String>(ARGUMENT_CAT_DETAILS_KEY).orEmpty()
        return Gson().fromJson(catStr, CatModel::class.java)
    }

    fun updateFavoriteCatState(cat: CatModel) {
        viewModelScope.launch {
            updateFavoriteCatStateUseCase.invoke(
                catId = cat.id,
                isFavorite = !cat.favoriteId.isNullOrBlank()
            ).collect { response ->
                stateFlow.value = response
                if (response.isSuccess()) {
                    response.data?.let { favoriteCat ->
                        catFlow.update {
                            it.copy(
                                favoriteId = favoriteCat.id
                            )
                        }
                    }
                }
            }
        }
    }

    fun dismissError() {
        stateFlow.value = ApiResponse.Start
    }
}