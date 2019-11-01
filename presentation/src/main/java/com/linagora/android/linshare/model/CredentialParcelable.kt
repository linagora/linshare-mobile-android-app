package com.linagora.android.linshare.model

import android.os.Parcelable
import com.linagora.android.linshare.domain.model.Username
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import java.net.URL

@Parcelize
@TypeParceler<Username, UsernameParceler>()
class CredentialParcelable(val baseUrl: URL, val username: Username) : Parcelable
