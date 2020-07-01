package com.linagora.android.linshare.domain.model

sealed class OperatorType {

    abstract class OfflineOperatorType : OperatorType()

    abstract class OnlineOperatorType : OperatorType()

    object SwiftRefresh : OnlineOperatorType()
}
