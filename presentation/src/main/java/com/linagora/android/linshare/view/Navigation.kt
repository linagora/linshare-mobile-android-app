package com.linagora.android.linshare.view

class Navigation {

    enum class LoginFlow {
        DIRECT,
        INDIRECT
    }

    enum class UploadType {
        OUTSIDE_APP,
        OUTSIDE_APP_TO_WORKGROUP,
        INSIDE_APP,
        INSIDE_APP_TO_WORKGROUP
    }

    enum class FileType {
        ROOT,
        NORMAL
    }
}
