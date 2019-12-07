package com.linagora.android.linshare.domain.model.upload

interface OnTransfer : (TransferredBytes, TotalBytes) -> Unit
