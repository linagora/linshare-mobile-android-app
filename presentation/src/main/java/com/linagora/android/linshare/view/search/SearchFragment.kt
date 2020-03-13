package com.linagora.android.linshare.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentSearchBinding
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.usecases.myspace.ContextMenuClick
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.dismissKeyboard
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.util.showKeyboard
import com.linagora.android.linshare.view.MainNavigationFragment
import kotlinx.android.synthetic.main.fragment_search.searchView
import kotlinx.android.synthetic.main.fragment_search.view.searchView
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import javax.inject.Inject

class SearchFragment : MainNavigationFragment() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchFragment::class.java)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentSearchBinding

    private lateinit var searchViewModel: SearchViewModel

    private lateinit var searchContextMenuDialog: SearchContextMenuDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        initViewModel(binding)
        return binding.root
    }

    private fun initViewModel(searchBinding: FragmentSearchBinding) {
        searchViewModel = getViewModel(viewModelFactory)
        searchBinding.lifecycleOwner = this
        searchBinding.searchViewModel = searchViewModel

        observeViewState()
    }

    private fun observeViewState() {
        searchViewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.map { success -> when (success) {
                is Success.ViewEvent -> reactToViewEvent(success)
            } }
        })
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is ContextMenuClick -> showContextMenu(viewEvent.document)
        }
        searchViewModel.dispatchState(Either.right(Success.Idle))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LOGGER.info("onViewCreated()")
        super.onViewCreated(view, savedInstanceState)

        setUpSearchView()
    }

    private fun setUpSearchView() {
        binding.toolbar.searchView.apply {

            setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    this@apply.dismissKeyboard()
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    LOGGER.info("onQueryTextChange() $newText")
                    newText.takeIf { it.isNotBlank() }
                        ?.let(::QueryString)
                        ?.let(this@SearchFragment::sendQueryString)
                    return true
                }
            })

            setOnQueryTextFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    view.findFocus().showKeyboard()
                }
            }
            requestFocus()
        }
    }

    private fun sendQueryString(query: QueryString) {
        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.queryChannel.send(query)
        }
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_in_white_bg)
    }

    override fun onPause() {
        searchView.dismissKeyboard()
        super.onPause()
    }

    private fun showContextMenu(document: Document) {
        searchView.dismissKeyboard()
        searchContextMenuDialog = SearchContextMenuDialog(document)
        searchContextMenuDialog.show(childFragmentManager, "search context menu dialog")
    }
}
