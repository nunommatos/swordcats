package pt.nunomatos.swordcats.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pt.nunomatos.swordcats.data.local.LocalDataSource
import pt.nunomatos.swordcats.data.remote.RemoteDataSource
import pt.nunomatos.swordcats.data.repository.CatsRepository
import pt.nunomatos.swordcats.data.repository.UserRepository
import pt.nunomatos.swordcats.domain.repository.ICatsRepository
import pt.nunomatos.swordcats.domain.repository.IUserRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideCatsRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ): ICatsRepository {
        return CatsRepository(localDataSource, remoteDataSource)
    }

    @Provides
    fun provideUserRepository(localDataSource: LocalDataSource): IUserRepository {
        return UserRepository(localDataSource)
    }
}