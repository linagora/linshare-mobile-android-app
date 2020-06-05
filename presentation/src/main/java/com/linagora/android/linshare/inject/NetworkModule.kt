package com.linagora.android.linshare.inject

import com.google.gson.GsonBuilder
import com.linagora.android.linshare.BuildConfig
import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.network.adapter.BaseErrorCodeDeserializer
import com.linagora.android.linshare.data.network.adapter.DateLongDeserializer
import com.linagora.android.linshare.data.network.adapter.DocumentIdDeserializer
import com.linagora.android.linshare.data.network.adapter.MailingListIdAdapter
import com.linagora.android.linshare.data.network.adapter.MediaTypeDeserializer
import com.linagora.android.linshare.data.network.adapter.QuotaIdAdapter
import com.linagora.android.linshare.data.network.adapter.QuotaSizeDeserializer
import com.linagora.android.linshare.data.network.adapter.ShareIdDeserializer
import com.linagora.android.linshare.data.network.adapter.SharedSpaceAccountIdAdapter
import com.linagora.android.linshare.data.network.adapter.SharedSpaceIdAdapter
import com.linagora.android.linshare.data.network.adapter.SharedSpaceMemberIdAdapter
import com.linagora.android.linshare.data.network.adapter.WorkGroupNodeIdAdapter
import com.linagora.android.linshare.data.network.factory.RuntimeTypeAdapterFactory
import com.linagora.android.linshare.domain.model.BaseErrorCode
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.MailingListAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.MailingListId
import com.linagora.android.linshare.domain.model.autocomplete.SimpleAutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.UserAutoCompleteResult
import com.linagora.android.linshare.domain.model.document.DocumentId
import com.linagora.android.linshare.domain.model.quota.QuotaId
import com.linagora.android.linshare.domain.model.quota.QuotaSize
import com.linagora.android.linshare.domain.model.share.ShareId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupFolder
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceAccountId
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMemberId
import com.linagora.android.linshare.network.AuthorizationInterceptor
import com.linagora.android.linshare.network.DynamicBaseUrlInterceptor
import com.linagora.android.linshare.util.Constant
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
    fun provideWorkgroupNodeTypeAdapterFactory(): RuntimeTypeAdapterFactory<WorkGroupNode> {
        return RuntimeTypeAdapterFactory
            .of(WorkGroupNode::class.java, "type")
            .registerSubtype(WorkGroupFolder::class.java, Constant.WORK_GROUP_TYPE_FOLDER)
            .registerSubtype(WorkGroupDocument::class.java, Constant.WORK_GROUP_TYPE_DOCUMENT)
    }

    @Singleton
    @Provides
    fun provideAutoCompleteResultTypeAdapterFactory(): RuntimeTypeAdapterFactory<AutoCompleteResult> {
        return RuntimeTypeAdapterFactory
            .of(AutoCompleteResult::class.java, "type")
            .registerSubtype(SimpleAutoCompleteResult::class.java, Constant.AUTO_COMPLETE_RESULT_TYPE_SIMPLE)
            .registerSubtype(UserAutoCompleteResult::class.java, Constant.AUTO_COMPLETE_RESULT_TYPE_USER)
            .registerSubtype(MailingListAutoCompleteResult::class.java, Constant.AUTO_COMPLETE_RESULT_TYPE_MAILING_LIST)
    }

    @Singleton
    @Provides
    fun provideLinShareRetrofit(
        clientBuilder: OkHttpClient.Builder,
        autoCompleteTypeAdapterFactory: RuntimeTypeAdapterFactory<AutoCompleteResult>,
        workGroupNodeTypeAdapterFactory: RuntimeTypeAdapterFactory<WorkGroupNode>
    ): Retrofit {

        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateLongDeserializer())
            .registerTypeAdapter(QuotaSize::class.java, QuotaSizeDeserializer())
            .registerTypeAdapter(MediaType::class.java, MediaTypeDeserializer())
            .registerTypeAdapter(BaseErrorCode::class.java, BaseErrorCodeDeserializer())
            .registerTypeAdapter(DocumentId::class.java, DocumentIdDeserializer())
            .registerTypeAdapter(ShareId::class.java, ShareIdDeserializer())
            .registerTypeAdapter(SharedSpaceId::class.java, SharedSpaceIdAdapter())
            .registerTypeAdapter(WorkGroupNodeId::class.java, WorkGroupNodeIdAdapter())
            .registerTypeAdapter(QuotaId::class.java, QuotaIdAdapter())
            .registerTypeAdapter(MailingListId::class.java, MailingListIdAdapter())
            .registerTypeAdapter(SharedSpaceMemberId::class.java, SharedSpaceMemberIdAdapter())
            .registerTypeAdapter(SharedSpaceAccountId::class.java, SharedSpaceAccountIdAdapter())
            .registerTypeAdapterFactory(workGroupNodeTypeAdapterFactory)
            .registerTypeAdapterFactory(autoCompleteTypeAdapterFactory)
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
