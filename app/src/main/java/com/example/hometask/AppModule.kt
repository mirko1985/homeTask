package com.example.hometask

import android.content.Context
import androidx.room.Room
import com.example.hometask.data.database.RickAndMortyServiceDatabase
import com.example.hometask.network.RickAndMortyService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val DB_NAME = "charactersDB"
    private const val BASE_URL = "https://rickandmortyapi.com/api/"
    const val PAGE_SIZE = 20

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun providesRickAndMortyService(retrofit: Retrofit): RickAndMortyService =
        retrofit.create(RickAndMortyService::class.java)


    @Singleton
    @Provides
    fun provideCharactersDatabase(@ApplicationContext appContext: Context): RickAndMortyServiceDatabase =
        Room.databaseBuilder(appContext, RickAndMortyServiceDatabase::class.java, DB_NAME).build()
}