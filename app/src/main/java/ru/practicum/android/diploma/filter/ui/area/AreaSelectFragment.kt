package ru.practicum.android.diploma.filter.ui.area

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentAreaSelectBinding
import ru.practicum.android.diploma.filter.domain.model.Location
import ru.practicum.android.diploma.global.util.CustomFragment

class AreaSelectFragment : CustomFragment<FragmentAreaSelectBinding>() {
    private val locations = mutableListOf<Location>()
    private val viewModel by viewModel<AreaSelectViewModel>()
    private var onAreaClickDebounce: ((Location) -> Unit)? = null
    private val adapter = AreaAdapter(locations) { location ->
        onAreaClickDebounce?.let { it(location) }
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAreaSelectBinding {
        return FragmentAreaSelectBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editText.doOnTextChanged { text, start, before, count ->
            renderEditTextIconsVisibility(text)
            viewModel.filterDebounce(text.toString())
        }
        binding.btBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.clearButton.setOnClickListener {
            binding.editText.setText("")
            val inputMethodManager =
                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.clearButton.windowToken, 0)
            binding.editText.clearFocus()
            viewModel.filter("")
        }
        onAreaClickDebounce = { area ->
            if (clickDebounce()) {
                viewModel.saveSelection(area)
                findNavController().popBackStack()
            }
        }
        binding.areaList.adapter = adapter
        binding.areaList.setHasFixedSize(false)
        viewModel.loadArea()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: AreaSelectState) {
        when (state) {
            is AreaSelectState.Content -> {
                setStateContent(state.locations)
            }

            is AreaSelectState.NotFound -> {
                setStateNotFound()
            }

            is AreaSelectState.Error -> {
                setStateError()
            }

            is AreaSelectState.Loading -> {
                setStateLoading()
            }
        }
    }

    private fun setStateContent(areasFromVM: List<Location>) {
        locations.clear()
        locations.addAll(areasFromVM)
        locations.sortBy { it.area.name }
        binding.areaList.isVisible = true
        binding.progressBar.isVisible = false
        binding.windowMessage.isVisible = false
        adapter.locations = locations
        adapter.notifyDataSetChanged()
    }

    private fun setStateLoading() {
        hideKeyboard()
        with(binding) {
            areaList.isVisible = false
            progressBar.isVisible = true
            windowMessage.isVisible = false
        }
    }

    private fun setStateError() {
        hideKeyboard()
        with(binding) {
            areaList.isVisible = false
            progressBar.isVisible = false
            windowMessage.isVisible = true
            textMessage.setText(getText(R.string.failed_to_get_list))
            textMessage.gravity = Gravity.CENTER
            imageMessage.setImageResource(R.drawable.image_magic_carpet)
            textMessage.isVisible = true
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.editText.windowToken, 0)
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

    private fun setStateNotFound() {
        hideKeyboard()
        with(binding) {
            areaList.isVisible = false
            progressBar.isVisible = false
            windowMessage.isVisible = true
            textMessage.setText(R.string.there_is_no_such_region)
            textMessage.isVisible = true
            imageMessage.setImageResource(R.drawable.image_no_list_vacancy)
        }
    }
}
