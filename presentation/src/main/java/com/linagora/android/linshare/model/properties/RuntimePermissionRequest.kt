package com.linagora.android.linshare.model.properties

sealed class RuntimePermissionRequest {

    object Initial : RuntimePermissionRequest()

    abstract class ReadStoragePermissionRequest : RuntimePermissionRequest()
    object ShouldShowReadStorage : ReadStoragePermissionRequest()
    object ShouldNotShowReadStorage : ReadStoragePermissionRequest()

    abstract class WriteStoragePermissionRequest : RuntimePermissionRequest()
    object ShouldShowWriteStorage : WriteStoragePermissionRequest()
    object ShouldNotShowWriteStorage : WriteStoragePermissionRequest()

    abstract class ReadContactPermissionRequest : RuntimePermissionRequest()
    object ShouldShowReadContact : ReadContactPermissionRequest()
    object ShouldNotShowReadContact : ReadContactPermissionRequest()
}
