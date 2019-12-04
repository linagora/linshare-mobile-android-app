package com.linagora.android.linshare.inject

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.linagora.android.linshare.data.repository.authentication.LinshareAuthenticationRepository
import com.linagora.android.linshare.data.repository.credential.PreferenceCredentialRepository
import com.linagora.android.linshare.data.repository.credential.PreferenceTokenRepository
import com.linagora.android.linshare.data.repository.document.DocumentRepositoryImp
import com.linagora.android.linshare.data.repository.user.LinshareAuditUserRepository
import com.linagora.android.linshare.data.repository.user.LinshareQuotaRepository
import com.linagora.android.linshare.data.repository.user.LinshareUserRepository
import com.linagora.android.linshare.domain.network.manager.AuthorizationManager
import com.linagora.android.linshare.domain.repository.CredentialRepository
import com.linagora.android.linshare.domain.repository.TokenRepository
import com.linagora.android.linshare.domain.repository.authentication.AuthenticationRepository
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.domain.repository.user.AuditUserRepository
import com.linagora.android.linshare.domain.repository.user.QuotaRepository
import com.linagora.android.linshare.domain.repository.user.UserRepository
import com.linagora.android.linshare.network.AuthorizationManagerImp
import com.linagora.android.linshare.view.LinShareApplication
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
}
