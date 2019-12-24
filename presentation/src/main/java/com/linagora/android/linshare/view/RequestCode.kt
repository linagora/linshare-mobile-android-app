package com.linagora.android.linshare.view

sealed class RequestCode(val code: Int)

sealed class PermissionRequestCode(code: Int) : RequestCode(code)

object ReadExternalPermissionRequestCode : PermissionRequestCode(1000)
