package ru.practicum.android.diploma.filter.ui.area

import ru.practicum.android.diploma.filter.domain.model.Location

sealed interface AreaSelectState {
    object Loading : AreaSelectState

    data class Content(val locations: List<Location>) : AreaSelectState

    object Error : AreaSelectState

    object NotFound : AreaSelectState
}
