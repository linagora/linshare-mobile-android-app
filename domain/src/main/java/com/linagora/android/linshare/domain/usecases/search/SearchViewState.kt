package com.linagora.android.linshare.domain.usecases.search

import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success

data class SearchViewState(val documents: List<Document>) : Success.ViewState()
data class SearchFailure(val throwable: Throwable) : Failure.FeatureFailure()
object NoResults : Failure.FeatureFailure()
object SearchInitial : Success.ViewState()
object OpenSearchView : Success.ViewEvent()
object CloseSearchView : Success.ViewEvent()
