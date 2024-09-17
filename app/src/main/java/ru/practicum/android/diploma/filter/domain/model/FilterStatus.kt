package ru.practicum.android.diploma.filter.domain.model

class FilterStatus(
    val country: Country?,
    val area: Area?,
    val industry: Industry?,
    val salary: Int?,
    val onlyWithSalary: Boolean = false,
) {
    var isFilterActive: Boolean = false

    fun isDefaultParams(): Boolean {
        return country == null && area == null && industry == null && salary == null && !onlyWithSalary
    }
}
