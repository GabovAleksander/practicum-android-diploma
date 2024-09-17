package ru.practicum.android.diploma.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.global.util.RequestResult
import ru.practicum.android.diploma.global.util.debounce
import ru.practicum.android.diploma.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.search.domain.model.SearchQuery
import ru.practicum.android.diploma.search.domain.model.Vacancy
import ru.practicum.android.diploma.search.domain.model.VacancyList

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>(SearchState.EmptyEditText)
    fun observeState(): LiveData<SearchState> = stateLiveData
    private var vacanciesList = mutableListOf<Vacancy>()
    private var lastSearchText: String? = null
    private var pages: Int = 0
    private var currentPage: Int = -1
    private var vacanciesFound: Int = 0
    private var isLoading: Boolean = false

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            pages = 0
            currentPage = -1
            vacanciesFound = 0
            vacanciesList.clear()
            search(changedText, currentPage)
        }

    fun searchDebounce(changedText: String) {
        if (lastSearchText != changedText) {
            lastSearchText = changedText
            trackSearchDebounce(changedText)
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    fun search(input: String, page: Int) {
        if (input.isNotEmpty()) {
            if (page == 0) {
                renderState(SearchState.Loading)
            }
            viewModelScope.launch {
                searchInteractor.getVacancies(
                    SearchQuery(
                        text = input,
                        page = page + 1,
                        perPage = VACANCIES_PER_PAGE
                    )
                )
                    .collect {
                        processResult(result = it)
                        isLoading = false
                    }
            }
        }
    }

    fun getNextPage() {
        if (currentPage < pages - 1 && !isLoading && !lastSearchText.isNullOrEmpty()) {
            isLoading = true
            renderState(SearchState.LoadingNewPage)
            search(lastSearchText!!, currentPage + 1)
        }
    }

    private fun processResult(result: RequestResult<VacancyList>) {
        when (result) {
            is RequestResult.Success -> {
                if (!lastSearchText.isNullOrEmpty() && result.data!!.found == 0) {
                    vacanciesList.clear()
                    renderState(SearchState.NotFound)
                } else {
                    val preparedData = result.data!!
                    vacanciesList.addAll(preparedData.items)
                    pages = result.data.pages
                    currentPage = result.data.page
                    vacanciesFound = result.data.found
                    val data = preparedData.copy(items = vacanciesList)
                    renderState(SearchState.Content(data))
                }
            }

            is RequestResult.Error -> {
                renderState(SearchState.Error(result.error!!, currentPage))
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val VACANCIES_PER_PAGE = 20
    }

}
