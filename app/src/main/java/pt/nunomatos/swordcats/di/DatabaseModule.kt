package pt.nunomatos.swordcats.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pt.nunomatos.swordcats.data.local.UserDao
import pt.nunomatos.swordcats.data.local.CatsDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideCatsDatabase(@ApplicationContext context: Context): CatsDatabase {
        return CatsDatabase.getDatabase(context)
    }

    @Provides
    fun providePhotosDao(database: CatsDatabase): UserDao {
        return database.userDao()
    }
}