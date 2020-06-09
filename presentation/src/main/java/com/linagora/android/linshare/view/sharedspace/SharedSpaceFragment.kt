package com.linagora.android.linshare.view.sharedspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSharedSpaceBinding
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.usecases.search.CloseSearchView
import com.linagora.android.linshare.domain.usecases.search.OpenSearchView
import com.linagora.android.linshare.domain.usecases.sharedspace.DetailsSharedSpaceItem
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceContextMenuClick
import com.linagora.android.linshare.domain.usecases.sharedspace.SharedSpaceItemClick
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.SharedSpaceNavigationInfo
import com.linagora.android.linshare.model.parcelable.WorkGroupNodeIdParcelable
import com.linagora.android.linshare.model.parcelable.toParcelable
import com.linagora.android.linshare.util.Constant.CLEAR_QUERY_STRING
import com.linagora.android.linshare.util.Constant.NOT_SUBMIT_TEXT
import com.linagora.android.linshare.util.dismissDialogFragmentByTag
import com.linagora.android.linshare.util.dismissKeyboard
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.util.showKeyboard
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.Navigation
import com.linagora.android.linshare.view.sharedspacedocument.SharedSpaceDocumentFragment.Companion.NAVIGATION_INFO_KEY
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SharedSpaceFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var sharedSpaceViewModel: SharedSpaceViewModel

    private lateinit var binding: FragmentSharedSpaceBinding

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SharedSpaceFragment::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSharedSpaceBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(binding: FragmentSharedSpaceBinding) {
        sharedSpaceViewModel = getViewModel(viewModelFactory)
        binding.lifecycleOwner = this
        binding.viewModel = sharedSpaceViewModel
        observeViewState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSwipeRefreshLayout()
        getSharedSpace()
        setUpSearchView()
    }

    private fun observeViewState() {
        sharedSpaceViewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.map { success ->
                when (success) {
                    is Success.ViewEvent -> reactToViewEvent(success)
                }
            }
        })
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is SharedSpaceItemClick -> navigateIntoSharedSpace(viewEvent.sharedSpaceNodeNested)
            is OpenSearchView -> handleOpenSearch()
            is CloseSearchView -> handleCloseSearch()
            is SharedSpaceContextMenuClick -> showContextMenu(viewEvent.sharedSpaceNodeNested)
            is DetailsSharedSpaceItem -> navigateToDetails(viewEvent.sharedSpaceNodeNested)
        }
        sharedSpaceViewModel.dispatchState(Either.right(Success.Idle))
    }

    private fun handleOpenSearch() {
        binding.apply {
            includeSearchContainer.searchContainer.visibility = View.VISIBLE
            includeSearchContainer.searchView.requestFocus()
            sharedSpaceBottomBar.visibility = View.GONE
        }
    }

    private fun handleCloseSearch() {
        binding.apply {
            includeSearchContainer.searchContainer.visibility = View.GONE
            sharedSpaceBottomBar.visibility = View.VISIBLE
            includeSearchContainer.searchView.apply {
                setQuery(CLEAR_QUERY_STRING, NOT_SUBMIT_TEXT)
                clearFocus()
            }
        }
        getSharedSpace()
    }

    private fun setUpSearchView() {
        binding.includeSearchContainer.searchView.apply {

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    this@apply.dismissKeyboard()
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    LOGGER.info("onQueryTextChange() $newText")
                    search(newText)
                    return true
                }
            })

            setOnQueryTextFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    view.findFocus().showKeyboard()
                }
            }
        }
    }

    private fun search(query: String) {
        query.trim()
            .let(::QueryString)
            .let(this@SharedSpaceFragment::sendQueryString)
    }

    private fun sendQueryString(query: QueryString) {
        sharedSpaceViewModel.searchWithQuery(query)
    }

    private fun setUpSwipeRefreshLayout() {
        binding.swipeLayoutSharedSpace.setColorSchemeResources(R.color.colorPrimary)
    }

    private fun showContextMenu(sharedSpaceNodeNested: SharedSpaceNodeNested) {
        dismissContextMenu()
        SharedSpaceContextMenuDialog(sharedSpaceNodeNested)
            .show(childFragmentManager, SharedSpaceContextMenuDialog.TAG)
    }

    private fun dismissContextMenu() {
        childFragmentManager.dismissDialogFragmentByTag(SharedSpaceContextMenuDialog.TAG)
    }

    private fun getSharedSpace() {
        LOGGER.info("getSharedSpaces")
        sharedSpaceViewModel.getSharedSpace()
    }

    private fun navigateToDetails(sharedSpaceNodeNested: SharedSpaceNodeNested) {
        dismissContextMenu()
        val actionToDetails = SharedSpaceFragmentDirections
            .actionToNavigationSharedSpaceDetails(sharedSpaceNodeNested.sharedSpaceId.toParcelable())
        findNavController().navigate(actionToDetails)
    }

    private fun navigateIntoSharedSpace(sharedSpaceNodeNested: SharedSpaceNodeNested) {
        val navigationBundle = bundleOf(
            NAVIGATION_INFO_KEY to generateNavigationInfoForSharedSpaceRoot(sharedSpaceNodeNested)
        )
        findNavController().navigate(R.id.navigation_shared_spaced_document, navigationBundle)
    }

    private fun generateNavigationInfoForSharedSpaceRoot(sharedSpaceNodeNested: SharedSpaceNodeNested): SharedSpaceNavigationInfo {
        return SharedSpaceNavigationInfo(
            sharedSpaceIdParcelable = sharedSpaceNodeNested.sharedSpaceId.toParcelable(),
            fileType = Navigation.FileType.ROOT,
            nodeIdParcelable = WorkGroupNodeIdParcelable(sharedSpaceNodeNested.sharedSpaceId.uuid)
        )
    }
}
