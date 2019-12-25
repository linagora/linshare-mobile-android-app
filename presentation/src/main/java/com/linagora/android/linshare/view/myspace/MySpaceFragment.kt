package com.linagora.android.linshare.view.myspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.linagora.android.linshare.databinding.FragmentMySpaceBinding
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MySpaceFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mySpaceViewModel: MySpaceViewModel

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MySpaceFragment::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMySpaceBinding.inflate(inflater, container, false)
        initViewModel()
        return binding.root
    }

    private fun initViewModel() {
        mySpaceViewModel = getViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LOGGER.info("onViewCreated")
        getAllDocuments()
    }

    private fun getAllDocuments() {
        LOGGER.info("getAllDocuments")
        mySpaceViewModel.getAllDocuments()
    }
}
