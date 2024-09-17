package ru.practicum.android.diploma.filter.ui.industry

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterIndustryBinding
import ru.practicum.android.diploma.filter.domain.model.Industry
import ru.practicum.android.diploma.global.util.CustomFragment
import ru.practicum.android.diploma.global.util.ResponseCodes

class FilterIndustryFragment : CustomFragment<FragmentFilterIndustryBinding>() {

    private val viewModel by viewModel<FilterIndustryViewModel>()
    private val industries = mutableListOf<Industry>()
    private var onVacancyClickDebounce: ((Industry) -> Unit)? = null
    private var selectIndustry: Industry? = null
    private val adapter = IndustryAdapter(industries) { industry ->
        onVacancyClickDebounce?.let { it(industry) }
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFilterIndustryBinding {
        return FragmentFilterIndustryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.industryList.adapter = adapter
        binding.industryList.layoutManager = LinearLayoutManager(requireContext())
        binding.clearButton.setOnClickListener {
            binding.editText.setText("")
            hideKeyboard()
            binding.editText.clearFocus()
        }
        binding.editText.doOnTextChanged { text, start, before, count ->
            renderEditTextIconsVisibility(text)
            viewModel.searchDebounce(text.toString())
        }
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        onVacancyClickDebounce = { industry ->
            selectIndustry = industry
            hideKeyboard()
            binding.buttonApply.isVisible = true
        }
        binding.buttonApply.setOnClickListener {
            viewModel.setIndustry(selectIndustry)
            binding.buttonApply.isVisible = false
            findNavController().navigateUp()
        }
        viewModel.fillData()
    }

    private fun renderEditTextIconsVisibility(s: CharSequence?) {
        with(binding) {
            if (s.isNullOrEmpty()) {
                editTextIconSearch.isVisible = true
                clearButton.isVisible = false
            } else {
                editTextIconSearch.isVisible = false
                clearButton.isVisible = true
            }
        }
    }

    private fun render(state: ScreenState) {
        binding.industryList.isVisible = false
        binding.windowProgressBar.isVisible = false
        binding.errorPlaceholder.isVisible = false
        when (state) {
            is ScreenState.Content -> {
                showContent(state.industries)
            }
            is ScreenState.Loading -> {
                binding.windowProgressBar.isVisible = true
            }
            is ScreenState.Error -> {
                showErrorPlaceHolder(state.errorCode)
            }
        }
    }

    private fun showContent(list: List<Industry>) {
        industries.clear()
        industries.addAll(list)
        adapter.notifyDataSetChanged()
        binding.industryList.isVisible = true
    }

    private fun showErrorPlaceHolder(errorCode: Int) {
        when (errorCode) {
            ResponseCodes.CODE_BAD_REQUEST -> {
                binding.imageMessage.setImageResource(R.drawable.image_error_server_cat)
                binding.textMessage.text = getString(R.string.server_error)
            }
            ResponseCodes.CODE_NO_CONNECT -> {
                binding.imageMessage.setImageResource(R.drawable.image_no_internet)
                binding.textMessage.text = getString(R.string.no_internet)
            }
        }
        binding.errorPlaceholder.isVisible = true
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.editText.windowToken, 0)
    }
}
