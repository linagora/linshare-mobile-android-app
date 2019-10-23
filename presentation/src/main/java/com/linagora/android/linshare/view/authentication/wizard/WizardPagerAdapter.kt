package com.linagora.android.linshare.view.authentication.wizard

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.linagora.android.linshare.R
import com.linagora.android.linshare.databinding.WizardPageLayoutBinding
import com.linagora.android.linshare.view.authentication.wizard.WizardPagerAdapter.WizardViewHolder
import java.security.InvalidParameterException

class WizardPagerAdapter(val context: Context) : RecyclerView.Adapter<WizardViewHolder>() {

    companion object {
        const val WIZARD_PAGE_TYPE = 0

        val PAGE_TYPES = listOf(WIZARD_PAGE_TYPE)

        private val WIZARD_PAGE_1 = WizardPage(
            R.drawable.ic_group,
            R.string.wizard_intro_one)

        private val WIZARD_PAGE_2 = WizardPage(
            R.drawable.ic_group,
            R.string.wizard_intro_two)

        private val WIZARD_PAGE_3 = WizardPage(
            R.drawable.ic_group,
            R.string.wizard_intro_three)

        val PAGES = listOf(WIZARD_PAGE_1, WIZARD_PAGE_2, WIZARD_PAGE_3)
    }

    private var wizardPageLayoutBinding: WizardPageLayoutBinding? = null

    override fun getItemViewType(position: Int): Int {
        return WIZARD_PAGE_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WizardViewHolder {
        return WizardViewHolder(
            when (viewType) {
                WIZARD_PAGE_TYPE -> createWizard(parent)
                else -> throw InvalidParameterException()
            }
        )
    }

    override fun getItemCount(): Int = PAGES.size

    override fun onBindViewHolder(holder: WizardViewHolder, position: Int) {
        val page = PAGES[position]
        holder.binding.txtPageIntro.text = context.getString(page.pageIntroRes)
    }

    private fun createWizard(parent: ViewGroup): WizardPageLayoutBinding {
        return WizardPageLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    class WizardViewHolder(val binding: WizardPageLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
