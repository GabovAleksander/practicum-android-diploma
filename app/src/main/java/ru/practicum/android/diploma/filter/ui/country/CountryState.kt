package ru.practicum.android.diploma.filter.ui.country

import ru.practicum.android.diploma.filter.domain.model.Country

sealed interface CountryState {
    data object Loading : CountryState

    data class Content(
        val region: List<Country>,
    ) : CountryState

    data class Error(val error: Int) : CountryState
}
