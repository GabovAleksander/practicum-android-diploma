package ru.practicum.android.diploma.filter.ui.area

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filter.domain.api.FilterInteractor
import ru.practicum.android.diploma.filter.domain.model.Country
import ru.practicum.android.diploma.filter.domain.model.FilterStatus
import ru.practicum.android.diploma.filter.domain.model.Location
import ru.practicum.android.diploma.global.util.Mapper
import ru.practicum.android.diploma.global.util.RequestResult
import ru.practicum.android.diploma.global.util.debounce
import ru.practicum.android.diploma.search.domain.api.SearchInteractor

class AreaSelectViewModel(
    private val filterInteractor: FilterInteractor,
    private val searchInteractor: SearchInteractor,
) : ViewModel() {
    private val stateLiveData = MutableLiveData<AreaSelectState>()
    private var lastSearchText: String? = null
    private var locations = mutableListOf<Location>()
    private val countries = mutableListOf<Country>()
    fun observeState(): LiveData<AreaSelectState> = stateLiveData

    private val areaSearchDebounce =
        debounce<String>(FILTER_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            filter(changedText)
        }

    fun filterDebounce(changedText: String) {
        if (lastSearchText != changedText) {
            lastSearchText = changedText
            areaSearchDebounce(changedText)
        }
    }

    fun loadArea() {
        viewModelScope.launch {
            searchInteractor.getCountries().collect { result ->
                processResultCountries(result.first, result.second)
            }
        }
        renderState(AreaSelectState.Loading)
        var locationList: List<Location>? = null
        if (filterInteractor.getFilterState().country != null) {
            val countryId = filterInteractor.getFilterState().country?.id
            val list = filterInteractor.getListPlaceWork().areas
            locationList = Mapper.getAreasByCountry(locations = list, countryId = countryId)
        } else {
            locationList = filterInteractor.getListPlaceWork().areas
        }

        if (locationList.isNullOrEmpty()) {
            viewModelScope.launch {
                searchInteractor.getAreas().collect { result ->
                    when (result) {
                        is RequestResult.Error -> {
                            renderState(AreaSelectState.Error(result.error!!))
                        }

                        is RequestResult.Success -> {
                            if (result.data?.areas.isNullOrEmpty()) {
                                renderState(AreaSelectState.NotFound)
                            } else {
                                processResult(result.data?.areas!!.toMutableList())
                            }
                        }
                    }
                }
            }
        } else {
            processResult(locationList)
        }

    }

    private fun processResult(listLocation: List<Location>) {
        val countryId = filterInteractor.getFilterState().country?.id
        locations = if (countryId != null) {
            Mapper.getAreasByCountry(listLocation, countryId).toMutableList()
        } else {
            Mapper.getAreasExceptOtherRegion(listLocation, countries).toMutableList()
        }

        when {
            listLocation.isEmpty() -> renderState(AreaSelectState.NotFound)
            countryId == null && countries.isEmpty() -> renderState(AreaSelectState.NotFound)
            else -> renderState(AreaSelectState.Content(locations))
        }
    }

    private fun processResultCountries(list: List<Country>?, error: Int?) {
        when {
            list != null -> countries.addAll(list)
            else -> countries.clear()
        }
    }

    fun filter(text: String) {
        val filteredList = mutableListOf<Location>()
        if (text.isEmpty()) {
            filteredList.addAll(locations)
        } else {
            for (item in locations) {
                if (item.area.name.contains(text, ignoreCase = true)) {
                    filteredList.add(item)
                }
            }
        }
        if (filteredList.size > 0) {
            renderState(AreaSelectState.Content(filteredList))
        } else {
            renderState(AreaSelectState.NotFound)
        }
    }

    fun saveSelection(location: Location) {
        val state = filterInteractor.getFilterState()
        val filterStatus =
            FilterStatus(
                country = location.country,
                area = location.area,
                industry = state.industry,
                salary = state.salary,
                onlyWithSalary = state.onlyWithSalary
            )

        filterInteractor.setFilterState(filterStatus)
    }

    private fun renderState(state: AreaSelectState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val FILTER_DEBOUNCE_DELAY = 2000L
    }
}
