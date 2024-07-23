package pt.nunomatos.swordcats.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pt.nunomatos.swordcats.BuildConfig
import pt.nunomatos.swordcats.data.model.NoNetworkException
import pt.nunomatos.swordcats.data.remote.CatsService
import pt.nunomatos.swordcats.common.hasInternetConnection
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val API_URL = "https://api.thecatapi.com/v1/"
    private const val REQUEST_HEADER_KEY_API_KEY = "x-api-key"
    private const val REQUEST_HEADER_VALUE_API_KEY =
        "live_2iey4uJ8CHdPOuEdYfRZOCdtaXrvi8jpL4VGUTueE6isoTE5rfxYXeHhc5XojHoJ"

    @Singleton
    @Provides
    fun provideHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val client = OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(Interceptor { chain ->
                if (!context.hasInternetConnection()) {
                    throw NoNetworkException()
                }

                chain.proceed(chain.request())
            })
            .addInterceptor(Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader(REQUEST_HEADER_KEY_API_KEY, REQUEST_HEADER_VALUE_API_KEY)
                    .build()
                chain.proceed(request)
            })

        if (BuildConfig.DEBUG) {
            client.addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
        }

        return client.build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideService(retrofit: Retrofit): CatsService {
        return retrofit.create(CatsService::class.java)
    }
}