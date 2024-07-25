package pt.nunomatos.swordcats.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import pt.nunomatos.swordcats.data.model.LoginState
import pt.nunomatos.swordcats.domain.use_case.GetLoginStateUseCase
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    getLoginStateUseCase: GetLoginStateUseCase
) : ViewModel() {

    private val userLoggedInFlow = MutableSharedFlow<LoginState>()
    val readUserLoggedInFlow = userLoggedInFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            getLoginStateUseCase.invoke().collect {
                userLoggedInFlow.emit(it)
            }
        }
    }
}