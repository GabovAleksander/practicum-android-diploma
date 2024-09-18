package ru.practicum.android.diploma.filter.ui.country

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filter.domain.api.FilterInteractor
import ru.practicum.android.diploma.filter.domain.model.Country
import ru.practicum.android.diploma.filter.domain.model.FilterStatus
import ru.practicum.android.diploma.filter.domain.model.Location
import ru.practicum.android.diploma.filter.domain.model.PlaceWork
import ru.practicum.android.diploma.global.util.RequestResult
import ru.practicum.android.diploma.search.domain.api.SearchInteractor

class CountryViewModel(
    private val searchInteractor: SearchInteractor,
    private val filterInteractor: FilterInteractor,
) : ViewModel() {

    private val stateLiveData = MutableLiveData<CountryState>()
    fun observeState(): LiveData<CountryState> = stateLiveData

    init {
        loadCountry()
    }

    private fun loadCountry() {
        renderState(CountryState.Loading)
        viewModelScope.launch {
            searchInteractor.getAreas()
                .collect() { result ->
                    when (result) {
                        is RequestResult.Error -> processResult(error = result.error, countriesList = null)
                        is RequestResult.Success -> {
                            processResult(result.data?.countries, null)
                            setListPlaceWork(result.data?.countries, result.data?.areas)
                        }
                    }
                }
        }
    }

    private fun processResult(countriesList: List<Country>?, error: Int?) {
        when {
            error != null -> {
                renderState(CountryState.Error(error))
            }

            else -> {
                renderState(
                    CountryState.Content(
                        region = countriesList!!
                    )
                )
            }
        }
    }

    private fun renderState(countryState: CountryState) {
        stateLiveData.postValue(countryState)
    }

    fun setCountryInfo(country: Country) {
        val oldFilterStatus = filterInteractor.getFilterState()
        val newFilterStatus = FilterStatus(
            country = country,
            area = oldFilterStatus.area,
            industry = oldFilterStatus.industry,
            salary = oldFilterStatus.salary,
            onlyWithSalary = oldFilterStatus.onlyWithSalary
        )
        filterInteractor.setFilterState(newFilterStatus)
    }

    private fun setListPlaceWork(listCountry: List<Country>?, listLocation: List<Location>?) {
        filterInteractor.setListPlaceWork(
            PlaceWork(
                countries = listCountry,
                areas = listLocation
            )
        )
    }
}
