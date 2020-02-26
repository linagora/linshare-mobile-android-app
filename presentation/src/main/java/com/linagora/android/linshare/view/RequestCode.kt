package com.linagora.android.linshare.view

sealed class RequestCode(val code: Int)

sealed class PermissionRequestCode(code: Int) : RequestCode(code)

object ReadExternalPermissionRequestCode : PermissionRequestCode(1000)
object WriteExternalPermissionRequestCode : PermissionRequestCode(1010)

object OpenDownloadViewRequestCode : PermissionRequestCode(2000)
