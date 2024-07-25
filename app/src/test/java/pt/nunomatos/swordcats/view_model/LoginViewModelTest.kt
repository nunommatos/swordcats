package pt.nunomatos.swordcats.view_model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import pt.nunomatos.swordcats.CoroutineTestRule
import pt.nunomatos.swordcats.data.model.UserModel
import pt.nunomatos.swordcats.domain.use_case.CreateUserUseCase
import pt.nunomatos.swordcats.domain.use_case.GetUserByEmailUseCase
import pt.nunomatos.swordcats.presentation.ui.login.LoginViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val rule = CoroutineTestRule(testDispatcher)

    private lateinit var viewModel: LoginViewModel
    private lateinit var createUserUseCase: CreateUserUseCase
    private lateinit var getUserByEmailUseCase: GetUserByEmailUseCase

    private suspend fun mockValidUser() {
        Mockito.`when`(getUserByEmailUseCase.invoke("email"))
            .thenReturn(UserModel(id = "", name = "", email = "email"))
    }

    private suspend fun mockInvalidUser() {
        Mockito.`when`(getUserByEmailUseCase.invoke("1")).thenReturn(null)
    }

    @Before
    fun setUp() {
        createUserUseCase = Mockito.mock(CreateUserUseCase::class.java)
        getUserByEmailUseCase = Mockito.mock(GetUserByEmailUseCase::class.java)
        viewModel = LoginViewModel(createUserUseCase, getUserByEmailUseCase)
    }

    @Test
    fun `Login Valid User`() {
        runTest {
            mockValidUser()
            val loginResults = mutableListOf<Unit>()

            val job = launch {
                viewModel.readLoginCompletedFlow.toList(loginResults)
            }

            viewModel.updateLoginEmail("email")
            assert(viewModel.readLoginUIState.value.email == "email")
            advanceUntilIdle()
            viewModel.login()
            advanceUntilIdle()
            assert(loginResults.size == 1)
            job.cancel()
        }
    }

    @Test
    fun `Login Invalid User`() {
        runTest {
            mockInvalidUser()
            val loginResults = mutableListOf<Unit>()

            val job = launch {
                viewModel.readLoginCompletedFlow.toList(loginResults)
            }
            viewModel.updateLoginEmail("1")
            assert(viewModel.readLoginUIState.value.email == "1")
            viewModel.login()
            assert(viewModel.readLoginUIState.value.createAccount)
            assert(loginResults.isEmpty())
            job.cancel()
        }
    }

    @Test
    fun `Login Invalid User And Register`() {
        runTest {
            mockInvalidUser()
            val loginResults = mutableListOf<Unit>()

            val job = launch {
                viewModel.readLoginCompletedFlow.toList(loginResults)
            }

            viewModel.updateLoginEmail("1")
            assert(viewModel.readLoginUIState.value.email == "1")
            viewModel.login()
            assert(viewModel.readLoginUIState.value.createAccount)
            viewModel.cancelRegistration()
            assert(!viewModel.readLoginUIState.value.createAccount)
            viewModel.updateLoginName("Name")
            viewModel.updateLoginEmail("Email")
            assert(viewModel.readLoginUIState.value.name == "Name")
            assert(viewModel.readLoginUIState.value.email == "Email")
            advanceUntilIdle()
            viewModel.registerUser()
            advanceUntilIdle()
            Mockito.verify(createUserUseCase).invoke("Name", "Email")
            assert(loginResults.size == 1)
            job.cancel()
        }
    }

    @Test
    fun `Login Invalid User And Cancel Registration`() {
        runTest {
            mockInvalidUser()

            viewModel.updateLoginEmail("1")
            assert(viewModel.readLoginUIState.value.email == "1")
            viewModel.login()
            assert(viewModel.readLoginUIState.value.createAccount)
            viewModel.cancelRegistration()
            assert(!viewModel.readLoginUIState.value.createAccount)
        }
    }

    @Test
    fun `Register User`() {
        runTest {
            val loginResults = mutableListOf<Unit>()

            val job = launch {
                viewModel.readLoginCompletedFlow.toList(loginResults)
            }
            viewModel.updateLoginName("Name")
            viewModel.updateLoginEmail("Email")
            assert(viewModel.readLoginUIState.value.name == "Name")
            assert(viewModel.readLoginUIState.value.email == "Email")
            advanceUntilIdle()
            viewModel.registerUser()
            advanceUntilIdle()
            Mockito.verify(createUserUseCase).invoke("Name", "Email")
            assert(loginResults.size == 1)
            job.cancel()
        }
    }
}