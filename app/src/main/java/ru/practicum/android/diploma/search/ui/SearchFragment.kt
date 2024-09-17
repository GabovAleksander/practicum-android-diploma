package ru.practicum.android.diploma.search.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.global.util.CustomFragment
import ru.practicum.android.diploma.global.util.Mapper
import ru.practicum.android.diploma.global.util.ResponseCodes
import ru.practicum.android.diploma.search.domain.model.Vacancy
import ru.practicum.android.diploma.search.domain.model.VacancyList

class SearchFragment : CustomFragment<FragmentSearchBinding>() {

    private val viewModel by viewModel<SearchViewModel>()
    private val vacancies = mutableListOf<Vacancy>()
    private var onVacancyClickDebounce: ((String) -> Unit)? = null
    private val adapter = VacancyAdapter(vacancies) { vacancy ->
        onVacancyClickDebounce?.let { it(vacancy.id) }
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vacancyList.adapter = adapter
        binding.vacancyList.setHasFixedSize(false)
        binding.editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.editText.text.isNullOrEmpty()) {
                render(SearchState.EmptyEditTextInFocus)
            }
        }
        binding.clearButton.setOnClickListener {
            binding.editText.setText("")
            val inputMethodManager =
                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.clearButton.windowToken, 0)
            binding.editText.clearFocus()
            render(SearchState.EmptyEditText)
        }
        binding.editText.doOnTextChanged { text, start, before, count ->
            renderEditTextIconsVisibility(text)
            viewModel.searchDebounce(text.toString())
        }
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        onVacancyClickDebounce = { vacancyId ->
            if (clickDebounce()) {
                val direction = SearchFragmentDirections.actionSearchFragmentToVacancyFragment(vacancyId)
                findNavController().navigate(direction)
            }
        }
        binding.vacancyList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val lastVisiblePosition =
                        (binding.vacancyList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (lastVisiblePosition >= adapter.itemCount - 1) {
                        viewModel.getNextPage()
                    }
                }
            }
        })

        binding.filter.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_filterSettingsFragment)
        }
    }

    private fun render(state: SearchState, newSearch: Boolean = false) {
        when (state) {
            is SearchState.EmptyEditText -> setStateEmptyEditText()

            is SearchState.Content -> setStateContent(state.vacancies)

            is SearchState.NotFound -> setStateNotFound()

            is SearchState.Error -> setStateError(state.error, state.currentPage)

            is SearchState.Loading -> setStateLoading()

            is SearchState.LoadingNewPage -> setStateLoadingNewPage()

            is SearchState.EmptyEditTextInFocus -> setStateEmptyTextInFocus()
        }
    }

    private fun setStateEmptyEditText() {
        binding.vacancyList.isVisible = false
        binding.progressBar.isVisible = false
        binding.recyclerProgressBar.isVisible = false
        binding.windowMessage.isVisible = true
        binding.countVacancies.isVisible = false
        binding.textMessage.isVisible = false
        binding.imageMessage.setImageResource(R.drawable.image_man_with_binoculars)
    }

    private fun setStateContent(vacanciesFromVM: VacancyList) {
        val countBeforeAdd = vacancies.count()
        vacancies.clear()
        vacancies.addAll(vacanciesFromVM.items)

        binding.vacancyList.isVisible = true
        binding.progressBar.isVisible = false
        binding.recyclerProgressBar.isVisible = false
        binding.windowMessage.isVisible = false
        binding.vacancyListLayout.isVisible = true
        binding.countVacancies.text = getString(
            R.string.founded_vacancies,
            vacanciesFromVM.found.toString(),
            Mapper.declineVacancy(requireContext(), vacanciesFromVM.found)
        )
        binding.countVacancies.isVisible = true
        adapter.vacancyList = vacancies
        if (vacanciesFromVM.page > 0 && countBeforeAdd < vacanciesFromVM.items.size) {
            adapter.notifyItemRangeInserted(countBeforeAdd, vacanciesFromVM.items.size - countBeforeAdd)
        } else {
            adapter.notifyDataSetChanged()
        }
    }

    private fun setStateNotFound() {
        with(binding) {
            vacancyList.isVisible = false
            progressBar.isVisible = false
            recyclerProgressBar.isVisible = false
            windowMessage.isVisible = true
            countVacancies.setText(R.string.no_such_vacancies)
            countVacancies.isVisible = true
            textMessage.setText(R.string.unable_to_retrieve_job_listing)
            textMessage.isVisible = true
            imageMessage.setImageResource(R.drawable.image_no_list_vacancy)
        }
    }

    private fun setStateError(err: Int, currentPage: Int) {
        if (currentPage == -1) {
            with(binding) {
                vacancyList.isVisible = false
                progressBar.isVisible = false
                recyclerProgressBar.isVisible = false
                windowMessage.isVisible = true
                countVacancies.isVisible = false
                if (err == ResponseCodes.CODE_NO_CONNECT) {
                    textMessage.setText(R.string.no_internet)
                    imageMessage.setImageResource(R.drawable.image_no_internet)
                } else {
                    textMessage.setText(R.string.server_error)
                    imageMessage.setImageResource(R.drawable.ic_error_server)
                }
                textMessage.isVisible = true
            }
        } else {
            if (err == ResponseCodes.CODE_NO_CONNECT) {
                binding.recyclerProgressBar.isVisible = false
                Toast.makeText(requireContext(), R.string.check_the_internet, Toast.LENGTH_LONG).show()
            } else {
                binding.recyclerProgressBar.isVisible = false
                Toast.makeText(requireContext(), R.string.error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setStateLoading() {
        hideKeyboard()
        with(binding) {
            vacancyListLayout.isVisible = false
            progressBar.isVisible = true
            windowMessage.isVisible = false
        }
    }

    private fun setStateLoadingNewPage() {
        hideKeyboard()
        with(binding) {
            vacancyList.isVisible = true
            progressBar.isVisible = false
            recyclerProgressBar.isVisible = true
            windowMessage.isVisible = false
            countVacancies.isVisible = true
            vacancyListLayout.isVisible = true
        }
    }

    private fun setStateEmptyTextInFocus() {
        with(binding) {
            vacancyList.isVisible = false
            progressBar.isVisible = false
            windowMessage.isVisible = false
            countVacancies.isVisible = false
        }
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

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.editText.windowToken, 0)
    }
}
