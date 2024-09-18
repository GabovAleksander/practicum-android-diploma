package ru.practicum.android.diploma.filter.ui.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.filter.domain.api.FilterInteractor
import ru.practicum.android.diploma.filter.domain.model.Area
import ru.practicum.android.diploma.filter.domain.model.Country
import ru.practicum.android.diploma.filter.domain.model.FilterStatus
import ru.practicum.android.diploma.global.util.Mapper

class LocationViewModel(private val filterInteractor: FilterInteractor) : ViewModel() {
    private val _selectedCountry = MutableLiveData<Country?>()
    val selectedCountry: LiveData<Country?> = _selectedCountry

    private val _selectedRegion = MutableLiveData<Area?>()
    val selectedRegion: LiveData<Area?> = _selectedRegion

    init {
        getFilterState()
    }

    fun getCountryAndRegion() {
        val country = filterInteractor.getFilterState().country
        val area = filterInteractor.getFilterState().area
        if (country != null) {
            _selectedCountry.postValue(country)
        }
        if (area != null) {
            _selectedRegion.postValue(area)
        }
    }

    private fun getFilterState() {
        val filterStatus = filterInteractor.loadFilterFromSharedPreferences()
        _selectedCountry.postValue(filterStatus.country)
        _selectedRegion.postValue(filterStatus.area)
    }

    fun resetCountry() {
        _selectedCountry.postValue(null)
    }

    fun resetRegion() {
        _selectedRegion.postValue(null)
    }

    fun setPlaceWork() {
        val filterStatus = filterInteractor.getFilterState()
        val newFilterStatus = FilterStatus(
            salary = filterStatus.salary,
            industry = filterStatus.industry,
            country = selectedCountry.value,
            area = selectedRegion.value,
            onlyWithSalary = filterStatus.onlyWithSalary
        )
        filterInteractor.setFilterState(
            newFilterStatus
        )
    }

    fun setCountryFromRegion(id: String) {
        val listRegions = filterInteractor.getListPlaceWork().areas
        val country = listRegions?.let { Mapper.getCountryByAreaId(id, it) }
        _selectedCountry.postValue(country)
    }

}
