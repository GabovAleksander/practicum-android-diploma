package ru.practicum.android.diploma.filter.domain.api

import ru.practicum.android.diploma.filter.domain.model.FilterStatus
import ru.practicum.android.diploma.filter.domain.model.PlaceWork

interface FilterInteractor {
    fun getFilterState(): FilterStatus
    fun setFilterState(filterState: FilterStatus)

    fun clearFilters()

    fun saveFilterToSharedPreferences(filterStatus: FilterStatus)
    fun loadFilterFromSharedPreferences(): FilterStatus

    fun setListPlaceWork(listPlaceWork: PlaceWork)
    fun getListPlaceWork(): PlaceWork
}
