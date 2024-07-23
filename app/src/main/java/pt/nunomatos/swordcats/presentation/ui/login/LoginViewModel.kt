package pt.nunomatos.swordcats.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.nunomatos.swordcats.domain.use_case.GetLoginStateUseCase
import pt.nunomatos.swordcats.domain.use_case.GetUserUseCase
import pt.nunomatos.swordcats.domain.use_case.RegisterUserUseCase
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    getLoginStateUseCase: GetLoginStateUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
) : ViewModel() {

    private val showRegistrationFormFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val readShowRegistrationFormFlow: StateFlow<Boolean> = showRegistrationFormFlow

    private val loginEmailFlow: MutableStateFlow<String> = MutableStateFlow("")
    val readLoginEmailFlow: StateFlow<String> = loginEmailFlow

    private val loginNameFlow: MutableStateFlow<String> = MutableStateFlow("")
    val readLoginNameFlow: StateFlow<String> = loginNameFlow

    val loginState = getLoginStateUseCase.invoke()

    fun login() {
        viewModelScope.launch {
            val user = getUserUseCase.invoke(readLoginEmailFlow.value)
            showRegistrationFormFlow.emit(user == null)
        }
    }

    fun registerUser() {
        viewModelScope.launch {
            registerUserUseCase.invoke(
                name = readLoginNameFlow.value,
                email = readLoginEmailFlow.value
            )
        }
    }

    fun updateLoginEmail(email: String) {
        loginEmailFlow.value = email
    }

    fun updateLoginName(name: String) {
        loginNameFlow.value = name
    }

    fun cancelRegistration() {
        showRegistrationFormFlow.value = false
    }
}