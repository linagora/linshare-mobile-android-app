package com.linagora.android.linshare.inject

import com.linagora.android.linshare.BuildConfig
import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.util.Constant.DEFAULT_LINSHARE_BASE_URL
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideHttpClientBuilder(
        dynamicBaseUrlInterceptor: DynamicBaseUrlInterceptor
    ): OkHttpClient.Builder {

        val builder = OkHttpClient.Builder()
        builder.addInterceptor(dynamicBaseUrlInterceptor)

        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logger)
        }

        builder.cache(null)
        return builder
    }

    @Singleton
    @Provides
    fun provideLinshareApi(
        clientBuilder: OkHttpClient.Builder
    ): LinshareApi {
        return Retrofit.Builder()
            .baseUrl(DEFAULT_LINSHARE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()
            .create(LinshareApi::class.java)
    }
}
