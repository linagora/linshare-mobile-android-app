/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

package com.linagora.android.linshare.inject

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import com.linagora.android.linshare.data.repository.authentication.LinshareAuthenticationRepository
import com.linagora.android.linshare.data.repository.autocomplete.LinshareAutoCompleteRepository
import com.linagora.android.linshare.data.repository.credential.PreferenceCredentialRepository
import com.linagora.android.linshare.data.repository.credential.PreferenceTokenRepository
import com.linagora.android.linshare.data.repository.document.DocumentRepositoryImp
import com.linagora.android.linshare.data.repository.download.RoomDownloadingRepository
import com.linagora.android.linshare.data.repository.order.PreferenceOrderListConfigurationRepository
import com.linagora.android.linshare.data.repository.properties.PreferencePropertiesRepository
import com.linagora.android.linshare.data.repository.share.ReceivedSharesRepositoryImp
import com.linagora.android.linshare.data.repository.user.LinshareAuditUserRepository
import com.linagora.android.linshare.data.repository.user.LinshareQuotaRepository
import com.linagora.android.linshare.data.repository.user.LinshareUserRepository
import com.linagora.android.linshare.domain.network.manager.AuthorizationManager
import com.linagora.android.linshare.domain.repository.CredentialRepository
import com.linagora.android.linshare.domain.repository.OrderListConfigurationRepository
import com.linagora.android.linshare.domain.repository.PropertiesRepository
import com.linagora.android.linshare.domain.repository.TokenRepository
import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import com.linagora.android.linshare.domain.repository.autocomplete.AutoCompleteRepository
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.domain.repository.download.DownloadingRepository
import com.linagora.android.linshare.domain.repository.functionality.FunctionalityRepository
import com.linagora.android.linshare.domain.repository.share.ReceivedShareRepository
import com.linagora.android.linshare.domain.repository.user.AuditUserRepository
import com.linagora.android.linshare.domain.repository.user.QuotaRepository
import com.linagora.android.linshare.domain.repository.user.UserRepository
import com.linagora.android.linshare.domain.usecases.upload.UploadInteractor
import com.linagora.android.linshare.domain.usecases.upload.UploadToSharedSpaceInteractor
import com.linagora.android.linshare.domain.usecases.utils.ViewStateStore
import com.linagora.android.linshare.functionality.FunctionalityObserver
import com.linagora.android.linshare.network.AuthorizationManagerImp
import com.linagora.android.linshare.notification.BaseNotification
import com.linagora.android.linshare.notification.UploadAndDownloadNotification
import com.linagora.android.linshare.operator.download.DownloadManagerOperator
import com.linagora.android.linshare.operator.download.DownloadOperator
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.util.DeviceStorageStats
import com.linagora.android.linshare.view.LinShareApplication
import com.linagora.android.linshare.view.upload.controller.UploadController
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class AppModule {

    @Provides
    open fun provideContext(application: LinShareApplication): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(application: LinShareApplication): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    @Provides
    @Singleton
    fun provideCredentialRepository(preferenceCredentialRepository: PreferenceCredentialRepository): CredentialRepository {
        return preferenceCredentialRepository
    }

    @Provides
    @Singleton
    fun provideTokenRepository(preferenceTokenRepository: PreferenceTokenRepository): TokenRepository {
        return preferenceTokenRepository
    }

    @Provides
    @Singleton
    fun provideAuthenticationRepository(linshareAuthenticationRepository: LinshareAuthenticationRepository): AuthenticationRepository {
        return linshareAuthenticationRepository
    }

    @Provides
    @Singleton
    fun provideAuditUserRepository(linshareAuditUserRepository: LinshareAuditUserRepository): AuditUserRepository {
        return linshareAuditUserRepository
    }

    @Provides
    @Singleton
    fun provideUserRepository(userRepository: LinshareUserRepository): UserRepository {
        return userRepository
    }

    @Provides
    @Singleton
    fun provideQuotaRepository(quotaRepository: LinshareQuotaRepository): QuotaRepository {
        return quotaRepository
    }

    @Provides
    @Singleton
    fun provideAuthorizationManager(authorizationManagerImp: AuthorizationManagerImp): AuthorizationManager {
        return authorizationManagerImp
    }

    @Provides
    @Singleton
    open fun provideDocumentRepository(documentRepository: DocumentRepositoryImp): DocumentRepository {
        return documentRepository
    }

    @Provides
    @Singleton
    fun provideUploadNotification(uploadAndDownloadNotification: UploadAndDownloadNotification): BaseNotification {
        return uploadAndDownloadNotification
    }

    @Provides
    @Singleton
    fun providePropertiesRepository(propertiesRepository: PreferencePropertiesRepository): PropertiesRepository {
        return propertiesRepository
    }

    @Provides
    @Singleton
    fun provideRoomDownloadingRepository(downloadingRepository: RoomDownloadingRepository): DownloadingRepository {
        return downloadingRepository
    }

    @Provides
    @Singleton
    fun provideDownloadManagerOperator(downloadManagerOperator: DownloadManagerOperator): DownloadOperator {
        return downloadManagerOperator
    }

    @Provides
    @Singleton
    fun provideReceivedSharesRepository(receivedSharesRepository: ReceivedSharesRepositoryImp): ReceivedShareRepository {
        return receivedSharesRepository
    }

    @Provides
    @Singleton
    fun provideAutoCompleteRepository(autoCompleteRepository: LinshareAutoCompleteRepository): AutoCompleteRepository {
        return autoCompleteRepository
    }

    @Provides
    @Singleton
    fun provideOrderListConfigurationRepository(orderListConfigurationRepository: PreferenceOrderListConfigurationRepository): OrderListConfigurationRepository {
        return orderListConfigurationRepository
    }

    @Provides
    @Singleton
    fun provideWorkManager(context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    fun provideUploadController(
        context: Context,
        uploadInteractor: UploadInteractor,
        uploadToSharedSpaceInteractor: UploadToSharedSpaceInteractor,
        viewStateStore: ViewStateStore
    ): UploadController {
        return UploadController(context, uploadInteractor, uploadToSharedSpaceInteractor, viewStateStore)
    }

    @Provides
    @Singleton
    fun provideContentResolver(context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideDeviceStorageStats(): DeviceStorageStats {
        return DeviceStorageStats()
    }

    @Provides
    @Singleton
    fun provideConnectionLiveData(context: Context): ConnectionLiveData {
        return ConnectionLiveData(context)
    }

    @Provides
    @Singleton
    fun provideFunctionalityObserver(
        functionalityRepository: FunctionalityRepository,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): FunctionalityObserver {
        return FunctionalityObserver(functionalityRepository, dispatcherProvider)
    }
}
