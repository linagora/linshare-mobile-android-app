package com.linagora.android.linshare.inject

import com.google.gson.GsonBuilder
import com.linagora.android.linshare.BuildConfig
import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.network.adapter.DateLongDeserializer
import com.linagora.android.linshare.data.network.adapter.DocumentIdDeserializer
import com.linagora.android.linshare.data.network.adapter.ErrorCodeDeserializer
import com.linagora.android.linshare.data.network.adapter.MediaTypeDeserializer
import com.linagora.android.linshare.data.network.adapter.QuotaSizeDeserializer
import com.linagora.android.linshare.domain.model.LinShareErrorCode
import com.linagora.android.linshare.domain.model.document.DocumentId
import com.linagora.android.linshare.domain.model.quota.QuotaSize
import com.linagora.android.linshare.network.AuthorizationInterceptor
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.util.Constant.DEFAULT_LINSHARE_BASE_URL
import com.linagora.android.linshare.util.Constant.DEFAULT_TIMEOUT_SECONDS
import com.linagora.android.linshare.util.Constant.NO_TIMEOUT
import dagger.Module
import dagger.Provides
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideHttpClientBuilder(
        dynamicBaseUrlInterceptor: DynamicBaseUrlInterceptor,
        authorizationInterceptor: AuthorizationInterceptor
    ): OkHttpClient.Builder {

        val builder = OkHttpClient.Builder()
        builder.addInterceptor(dynamicBaseUrlInterceptor)
            .addInterceptor(authorizationInterceptor)
            .connectTimeout(DEFAULT_TIMEOUT_SECONDS, SECONDS)
            .readTimeout(NO_TIMEOUT, SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT_SECONDS, SECONDS)

        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.HEADERS
            builder.addInterceptor(logger)
        }

        builder.cache(null)
        return builder
    }

    @Singleton
    @Provides
    fun provideLinShareRetrofit(clientBuilder: OkHttpClient.Builder): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateLongDeserializer())
            .registerTypeAdapter(QuotaSize::class.java, QuotaSizeDeserializer())
            .registerTypeAdapter(MediaType::class.java, MediaTypeDeserializer())
            .registerTypeAdapter(LinShareErrorCode::class.java, ErrorCodeDeserializer())
            .registerTypeAdapter(DocumentId::class.java, DocumentIdDeserializer())
            .create()
        return Retrofit.Builder()
            .baseUrl(DEFAULT_LINSHARE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(clientBuilder.build())
            .build()
    }

    @Singleton
    @Provides
    fun provideLinShareApi(linShareRetrofit: Retrofit): LinshareApi {
        return linShareRetrofit.create(LinshareApi::class.java)
    }
}
