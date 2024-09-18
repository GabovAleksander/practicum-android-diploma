package ru.practicum.android.diploma.filter.ui.industry

import ru.practicum.android.diploma.filter.domain.model.Industry

interface ScreenState {
    data object Loading : ScreenState
    data class Content(val industries: List<Industry>) : ScreenState
    data class Error(val errorCode: Int) : ScreenState
}
