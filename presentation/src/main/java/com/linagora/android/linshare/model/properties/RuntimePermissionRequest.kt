package com.linagora.android.linshare.model.properties

sealed class RuntimePermissionRequest {

    object Initial: RuntimePermissionRequest()

    abstract class ReadStoragePermissionRequest: RuntimePermissionRequest()
    object ShouldShowReadStorage: ReadStoragePermissionRequest()
    object ShouldNotShowReadStorage: ReadStoragePermissionRequest()
}
