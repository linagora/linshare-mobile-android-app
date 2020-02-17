package com.linagora.android.linshare.model.properties

sealed class RuntimePermissionRequest {

    abstract class ReadStoragePermissionRequest: RuntimePermissionRequest()
    object InitialReadStorage: ReadStoragePermissionRequest()
    object ShouldShowReadStorage: ReadStoragePermissionRequest()
    object ShouldNotShowReadStorage: ReadStoragePermissionRequest()
}
