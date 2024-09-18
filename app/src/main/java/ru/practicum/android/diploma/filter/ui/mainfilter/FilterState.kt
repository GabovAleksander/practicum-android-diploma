package ru.practicum.android.diploma.filter.ui.mainfilter

import ru.practicum.android.diploma.filter.domain.model.FilterStatus

sealed interface FilterState {
    data class Content(
        val filterStatus: FilterStatus
    ) : FilterState

    data object Empty : FilterState
}
