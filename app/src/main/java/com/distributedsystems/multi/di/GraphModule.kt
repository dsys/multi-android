package com.distributedsystems.multi.di

import android.content.Context
import android.util.Log
import com.apollographql.apollo.ApolloClient
import dagger.Module
import dagger.Provides
import io.reactivex.annotations.NonNull
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [AppModule::class])
class GraphModule {

    companion object {
        val LOG_TAG = GraphModule::class.java.canonicalName
    }

    @Provides
    @Singleton
    fun loggingInterceptor() : HttpLoggingInterceptor = HttpLoggingInterceptor({
        Log.i(LOG_TAG, it)
    }).setLevel(HttpLoggingInterceptor.Level.BASIC)

    @Provides
    @Singleton
    fun cacheFile(context: Context) : File = File(context.cacheDir, "okhttp_cache")

    @Provides
    @Singleton
    fun cache(cacheFile: File) : Cache = Cache(cacheFile, 10 * 1000 * 1000)

    @Provides
    @Singleton
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor, cache: Cache) : OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .cache(cache)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()

    @Provides
    @Singleton
    fun provideApolloClient (@NonNull okHttpClient: OkHttpClient) : ApolloClient =
            ApolloClient.builder()
                    .serverUrl("api-staging.multi.app")
                    .okHttpClient(okHttpClient)
                    .build()

}