package ru.practicum.android.diploma.filter.ui.mainfilter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.filter.domain.api.FilterInteractor
import ru.practicum.android.diploma.filter.domain.model.FilterStatus

class FilterSettingsViewModel(private val filterInteractor: FilterInteractor) : ViewModel() {

    private val filterSettingsState = MutableLiveData<FilterState>() // для примера сделал filter state

    fun getFilterState(): LiveData<FilterState> = filterSettingsState

    fun setSalary(salary: Int?) {
        val status = filterInteractor.getFilterState()
        val filterState = FilterStatus(
            salary = salary,
            industry = status.industry,
            country = status.country,
            area = status.area,
            onlyWithSalary = status.onlyWithSalary
        )
        filterInteractor.setFilterState(filterState)
        filterSettingsState.postValue(FilterState.Content(filterState))
    }

    fun setOnlyWithSalary(onlySalary: Boolean) {
        val status = filterInteractor.getFilterState()
        val filterState = FilterStatus(
            salary = status.salary,
            industry = status.industry,
            country = status.country,
            area = status.area,
            onlyWithSalary = onlySalary
        )
        filterInteractor.setFilterState(filterState)
        filterSettingsState.postValue(FilterState.Content(filterState))
    }

    fun getStateAreaAndIndustry() {
        val data = filterInteractor.getFilterState()
        if (!data.isDefaultParams()) {
            filterSettingsState.postValue(FilterState.Content(data))
        }
    }

    fun getSettingsFilter() {
        val prefs = filterInteractor.loadFilterFromSharedPreferences()
        filterInteractor.setFilterState(prefs)
        filterSettingsState.postValue(
            FilterState.Content(prefs)
        )
    }

    fun saveSettingsFilter() {
        filterInteractor.saveFilterToSharedPreferences(filterInteractor.getFilterState())
    }

    fun resetSettings() {
        filterSettingsState.postValue(
            FilterState.Empty
        )
        filterInteractor.clearFilters()
    }
}
