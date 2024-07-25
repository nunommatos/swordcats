package pt.nunomatos.swordcats.use_case

import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import pt.nunomatos.swordcats.domain.use_case.GetUserByEmailUseCase
import pt.nunomatos.swordcats.repository.FakeUserRepository

class GetUserByEmailUseCaseTest {
    private lateinit var userRepository: FakeUserRepository
    private lateinit var getUserByEmailUseCase: GetUserByEmailUseCase

    @Before
    fun setUp() {
        userRepository = FakeUserRepository()
        getUserByEmailUseCase = GetUserByEmailUseCase(userRepository)
    }

    @Test
    fun `Get User Valid Email`() {
        runTest {
            val user = getUserByEmailUseCase.invoke("email")
            assert(user != null)
        }
    }

    @Test
    fun `Get User Wrong Email`() {
        runTest {
            val user = getUserByEmailUseCase.invoke("")
            assert(user == null)
        }
    }
}