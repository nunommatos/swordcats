package pt.nunomatos.swordcats.presentation.ui.splash

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pt.nunomatos.swordcats.domain.use_case.GetLoginStateUseCase
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    getLoginStateUseCase: GetLoginStateUseCase
) : ViewModel() {
    val loginState = getLoginStateUseCase.invoke()
}