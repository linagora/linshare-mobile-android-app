package com.linagora.android.linshare.view.dialog

class DialogProperties {

    sealed class BottomDialogHeightRatio(val ratio: Float) {
        object ConfirmDialogHeightRatio : BottomDialogHeightRatio(0.4f)

        object ReadStorageExplanationDialogHeightRatio : BottomDialogHeightRatio(0.6f)
    }
}
