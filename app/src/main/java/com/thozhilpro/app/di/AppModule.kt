package com.thozhilpro.app.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.local.PreferencesManager
import com.thozhilpro.app.data.model.RefreshTokenRequest
import com.thozhilpro.app.data.model.RefreshTokenResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Change this to your server IP/URL
    private const val BASE_URL = "http://10.0.2.2:8080/api/"
    //private const val BASE_URL = "https://www.thozhilpro.com/api/"

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideOkHttpClient(preferencesManager: PreferencesManager, gson: Gson): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val token = runBlocking { preferencesManager.getToken() }
            val request = chain.request().newBuilder().apply {
                if (!token.isNullOrEmpty()) {
                    addHeader("Authorization", "Bearer $token")
                }
                addHeader("Content-Type", "application/json")
            }.build()
            chain.proceed(request)
        }

        val tokenAuthenticator = object : Authenticator {
            override fun authenticate(route: Route?, response: Response): Request? {
                if (response.request.url.encodedPath.contains("auth/refresh-token")) return null
                if (responseCount(response) > 1) return null

                val refreshToken = runBlocking { preferencesManager.getRefreshToken() } ?: return null

                val refreshBody = gson.toJson(RefreshTokenRequest(refreshToken))
                val refreshRequest = Request.Builder()
                    .url(BASE_URL + "auth/refresh-token")
                    .post(refreshBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val refreshClient = OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build()

                return try {
                    val refreshResponse = refreshClient.newCall(refreshRequest).execute()
                    if (refreshResponse.isSuccessful) {
                        val body = refreshResponse.body?.string()
                        val tokens = gson.fromJson(body, RefreshTokenResponse::class.java)
                        runBlocking { preferencesManager.saveTokens(tokens.token, tokens.refreshToken) }
                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${tokens.token}")
                            .build()
                    } else {
                        runBlocking { preferencesManager.clearAuthData() }
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }

            private fun responseCount(response: Response): Int {
                var count = 1
                var r = response.priorResponse
                while (r != null) {
                    count++
                    r = r.priorResponse
                }
                return count
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context,
        gson: Gson
    ): PreferencesManager {
        return PreferencesManager(context, gson)
    }
}
