package com.linagora.android.linshare.view.sharedspace.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import arrow.core.Either
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.FragmentAddMemberBinding
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType
import com.linagora.android.linshare.domain.model.autocomplete.ThreadMemberAutoCompleteRequest
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.model.sharedspace.member.AddMemberRequest
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceAccountId
import com.linagora.android.linshare.domain.usecases.sharedspace.role.OnSelectRoleClick
import com.linagora.android.linshare.domain.usecases.sharedspace.role.OnSelectedRole
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.model.parcelable.toSharedSpaceId
import com.linagora.android.linshare.util.binding.bindingDefaultSelectedRole
import com.linagora.android.linshare.util.binding.bindingRoles
import com.linagora.android.linshare.util.binding.clearFocus
import com.linagora.android.linshare.util.binding.initView
import com.linagora.android.linshare.util.binding.onSelectedMember
import com.linagora.android.linshare.util.binding.queryAfterTextChange
import com.linagora.android.linshare.util.binding.showKeyBoard
import com.linagora.android.linshare.util.dismissDialogFragmentByTag
import com.linagora.android.linshare.util.getViewModel
import com.linagora.android.linshare.view.MainNavigationFragment
import com.linagora.android.linshare.view.dialog.SelectRoleDialog
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.UUID
import javax.inject.Inject

class SharedSpaceAddMemberFragment : MainNavigationFragment() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SharedSpaceAddMemberFragment::class.java)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SharedSpaceAddMemberViewModel

    private lateinit var binding: FragmentAddMemberBinding

    private val arguments: SharedSpaceAddMemberFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMemberBinding.inflate(inflater, container, false)
        initViewModel()
        initAddMembersAutoComplete()
        return binding.root
    }

    private fun initViewModel() {
        viewModel = getViewModel(viewModelFactory)
        binding.lifecycleOwner = this
        binding.sharedSpaceId = arguments.sharedSpaceId.toSharedSpaceId()
        binding.viewModel = viewModel
        observeViewState()
    }

    private fun observeViewState() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            state.map { success -> when (success) {
                is Success.ViewState -> reactToViewState(success)
                is Success.ViewEvent -> reactToViewEvent(success)
            } }
        })
    }

    private fun initAddMembersAutoComplete() {
        with(binding.addMembersContainer) {
            initView()
            queryAfterTextChange(this@SharedSpaceAddMemberFragment::searchMembers)
            onSelectedMember(this@SharedSpaceAddMemberFragment::onAddMember)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSharedSpaceRoles()
    }

    private fun reactToViewState(viewState: Success.ViewState) {
        binding.addMembersContainer.apply {
            showKeyBoard(viewState)
            bindingRoles(viewState)
            bindingDefaultSelectedRole(viewState)
        }
    }

    private fun reactToViewEvent(viewEvent: Success.ViewEvent) {
        when (viewEvent) {
            is OnSelectRoleClick -> selectRoles(viewEvent.lastSelectedRole)
            is OnSelectedRole -> onSelectedRole(viewEvent.selectedRole)
        }
        viewModel.dispatchState(Either.right(Success.Idle))
    }

    private fun selectRoles(lastSelectedRole: SharedSpaceRole) {
        if (binding.addMembersContainer.sharedSpaceRoles != null) {
            dismissSelectRoleDialog()
            val roles = binding.addMembersContainer.sharedSpaceRoles!!
            SelectRoleDialog(roles.toList(), lastSelectedRole)
                .show(childFragmentManager, SelectRoleDialog.TAG)
        }
    }

    private fun onSelectedRole(selectedRole: SharedSpaceRole) {
        LOGGER.info("onSelectedRole(): $selectedRole")
        dismissSelectRoleDialog()
        binding.addMembersContainer.selectedRole = selectedRole
    }

    private fun dismissSelectRoleDialog() {
        childFragmentManager.dismissDialogFragmentByTag(SelectRoleDialog.TAG)
    }

    private fun searchMembers(autoCompletePattern: AutoCompletePattern, autoCompleteType: AutoCompleteType, sharedSpaceId: SharedSpaceId) {
        val threadMemberAutoCompleteRequest = ThreadMemberAutoCompleteRequest(autoCompletePattern, autoCompleteType, sharedSpaceId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addMemberSuggestionManager.query(threadMemberAutoCompleteRequest)
        }
    }

    private fun onAddMember(sharedSpaceId: SharedSpaceId, autoCompleteResult: AutoCompleteResult, role: SharedSpaceRole) {
        val addMemberRequest = AddMemberRequest(
            SharedSpaceAccountId(UUID.fromString(autoCompleteResult.identifier)),
            sharedSpaceId,
            role.sharedSpaceRoleId
        )
        viewModel.addMemberToSharedSpace(addMemberRequest)
    }

    override fun configureToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    }

    override fun onPause() {
        super.onPause()
        binding.addMembersContainer.clearFocus()
    }
}
