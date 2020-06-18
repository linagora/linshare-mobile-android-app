package com.linagora.android.linshare.view.base

import com.linagora.android.linshare.domain.model.workgroup.NewNameRequest

interface OnSelectedCreateBehavior {

    fun onCreate(newNameRequest: NewNameRequest)
}
