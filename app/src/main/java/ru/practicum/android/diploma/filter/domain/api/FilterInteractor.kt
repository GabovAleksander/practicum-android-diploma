package ru.practicum.android.diploma.filter.domain.api

import ru.practicum.android.diploma.filter.domain.model.Area
import ru.practicum.android.diploma.filter.domain.model.Country
import ru.practicum.android.diploma.filter.domain.model.Industry

interface FilterInteractor {
    fun getCity(): Country?
    fun setCity(city: Country?)

    fun getArea(): Area?
    fun setArea(area: Area?)

    fun getIndustry(): Industry?
    fun setIndustry(industry: Industry?)

    fun setOnlyWithSalary(status: Boolean)

    fun clearFilters()
}
