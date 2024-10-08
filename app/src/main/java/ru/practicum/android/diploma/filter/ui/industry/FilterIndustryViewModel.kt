package ru.practicum.android.diploma.filter.ui.industry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filter.domain.api.FilterInteractor
import ru.practicum.android.diploma.filter.domain.model.FilterStatus
import ru.practicum.android.diploma.filter.domain.model.Industry
import ru.practicum.android.diploma.global.util.RequestResult
import ru.practicum.android.diploma.global.util.debounce
import ru.practicum.android.diploma.search.domain.api.SearchInteractor

class FilterIndustryViewModel(
    private val searchInteractor: SearchInteractor,
    private val filterInteractor: FilterInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<ScreenState>()
    fun observeState(): LiveData<ScreenState> = stateLiveData

    private var industries = mutableListOf<Industry>()
    private var lastSearchText: String? = null

    private fun renderState(state: ScreenState) {
        stateLiveData.postValue(state)
    }

    fun fillData() {
        renderState(ScreenState.Loading)
        viewModelScope.launch {
            searchInteractor.getIndustries().collect {
                processResult(it)
            }
        }
    }

    private fun processResult(result: RequestResult<List<Industry>>) {
        when (result) {
            is RequestResult.Success -> {
                val preparedData = result.data!!
                industries.addAll(preparedData.sortedBy { it.name })
                if (industries.size > 0) {
                    renderState(ScreenState.Content(industries))
                } else {
                    renderState(ScreenState.Error(result.error!!))
                }
            }

            is RequestResult.Error -> {
                renderState(ScreenState.Error(result.error!!))
            }
        }
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchText != changedText) {
            lastSearchText = changedText
            industrySearchDebounce(changedText)
        }
    }

    private val industrySearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            search(changedText)
        }

    fun search(input: String) {
        val filteredIndustries = industries.filter {
            it.name.lowercase().startsWith(input.lowercase())
        }
        if (filteredIndustries.isNotEmpty()) {
            renderState(ScreenState.Content(filteredIndustries))
        } else {
            renderState(ScreenState.NotFound)
        }
    }

    fun getSelectedIndustry(): Industry? {
        val tempFilter = filterInteractor.getFilterState()
        return tempFilter.industry
    }

    fun setIndustry(newIndustry: Industry?) {
        val tempFilter = filterInteractor.getFilterState()
        with(tempFilter) {
            val newFilterStatus = FilterStatus(
                country = country,
                area = area,
                industry = newIndustry,
                salary = salary,
                onlyWithSalary = onlyWithSalary
            )
            filterInteractor.setFilterState(newFilterStatus)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
