package pt.nunomatos.swordcats.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.nunomatos.swordcats.domain.model.LoginUIState
import pt.nunomatos.swordcats.domain.use_case.CreateUserUseCase
import pt.nunomatos.swordcats.domain.use_case.GetUserByEmailUseCase
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserByEmailUseCase: GetUserByEmailUseCase,
) : ViewModel() {

    private val loginUIState: MutableStateFlow<LoginUIState> = MutableStateFlow(LoginUIState())
    val readLoginUIState = loginUIState.asStateFlow()

    private val loginCompletedFlow: MutableSharedFlow<Unit> = MutableSharedFlow()
    val readLoginCompletedFlow = loginCompletedFlow.asSharedFlow()

    fun login() {
        viewModelScope.launch {
            getUserByEmailUseCase(loginUIState.value.email).let { user ->
                if (user != null) {
                    loginCompletedFlow.emit(Unit)
                } else {
                    loginUIState.update {
                        it.copy(createAccount = true)
                    }
                }
            }
        }
    }

    fun registerUser() {
        viewModelScope.launch {
            createUserUseCase.invoke(
                name = loginUIState.value.name,
                email = loginUIState.value.email
            )
            loginCompletedFlow.emit(Unit)
        }
    }

    fun updateLoginEmail(email: String) {
        loginUIState.update { it.copy(email = email) }
    }

    fun updateLoginName(name: String) {
        loginUIState.update { it.copy(name = name) }
    }

    fun cancelRegistration() {
        loginUIState.update { it.copy(createAccount = false) }
    }
}