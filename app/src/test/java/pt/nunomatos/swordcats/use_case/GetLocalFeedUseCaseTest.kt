package pt.nunomatos.swordcats.use_case

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import pt.nunomatos.swordcats.data.model.UserFeedModel
import pt.nunomatos.swordcats.domain.use_case.GetLocalFeedUseCase
import pt.nunomatos.swordcats.repository.FakeCatsRepository

class GetLocalFeedUseCaseTest {

    private lateinit var getLocalFeedUseCase: GetLocalFeedUseCase
    private lateinit var fakeCatsRepository: FakeCatsRepository

    @Before
    fun setUp() {
        fakeCatsRepository = FakeCatsRepository()
        getLocalFeedUseCase = GetLocalFeedUseCase(fakeCatsRepository)
    }

    @Test
    fun `Get Local Feed`() {
        runTest {
            val responses = mutableListOf<UserFeedModel>()
            launch {
                getLocalFeedUseCase.invoke().toList(responses)
                assert(responses.size == 2)

                val initialFeed = responses.first()
                val finalFeed = responses.last()

                assert(initialFeed.feedPage == 0)
                assert(finalFeed.feedPage == 1)
                assert(initialFeed.updatedAt == 0L)
                assert(finalFeed.updatedAt == 1L)
                assert(initialFeed.cats.isEmpty())
                assert(finalFeed.cats.size == 1)
            }
        }
    }
}