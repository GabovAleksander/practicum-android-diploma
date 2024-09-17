package ru.practicum.android.diploma.filter.data

import ru.practicum.android.diploma.filter.domain.api.FilterRepository
import ru.practicum.android.diploma.filter.domain.model.Country
import ru.practicum.android.diploma.filter.domain.model.FilterStatus
import ru.practicum.android.diploma.filter.domain.model.Location
import ru.practicum.android.diploma.filter.domain.model.PlaceWork
import ru.practicum.android.diploma.global.sharedpreferences.SharedPreferencesFilter

class FilterRepositoryImpl(private val sharedPreferencesFilter: SharedPreferencesFilter) :
    FilterRepository {
    private var filterStatus: FilterStatus = FilterStatus(null, null, null, null, false)

    private var listCountries = mutableListOf<Country>()
    private var listLocation = mutableListOf<Location>()

    override fun getFilterStatus(): FilterStatus {
        return filterStatus
    }

    override fun setFilterStatus(filterStatus: FilterStatus) {
        val stat = FilterStatus(
            filterStatus.country,
            filterStatus.area,
            filterStatus.industry,
            filterStatus.salary,
            filterStatus.onlyWithSalary
        )
        stat.isFilterActive = filterStatus.isFilterActive
        this.filterStatus = stat
    }

    override fun clearFilters() {
        filterStatus = FilterStatus(null, null, null, null, false)
    }

    override fun saveFilterToSharedPreferences(filterStatus: FilterStatus) {
        sharedPreferencesFilter.saveFilterState(filterStatus)
    }

    override fun loadFilterFromSharedPreferences(): FilterStatus {
        return sharedPreferencesFilter.getFilterState()
    }

    override fun setListPlaceWork(listPlaceWork: PlaceWork) {
        listPlaceWork.areas?.let { listLocation.addAll(it) }
        listPlaceWork.countries?.let { listCountries.addAll(it) }
    }

    override fun getListPlaceWork(): PlaceWork {
        return PlaceWork(
            countries = listCountries,
            areas = listLocation
        )
    }

}
