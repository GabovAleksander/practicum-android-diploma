package ru.practicum.android.diploma.global.sharedpreferences

import ru.practicum.android.diploma.filter.domain.model.FilterStatus

interface SharedPreferencesFilter {
    fun saveFilterState(filterParam: FilterStatus)
    fun getFilterState(): FilterStatus
}
