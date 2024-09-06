package ru.practicum.android.diploma.global.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.search.ui.SearchViewModel
import ru.practicum.android.diploma.vacancy.ui.viewmodel.DetailsVacancyViewModel

val viewModelModule = module {

    viewModel {
        SearchViewModel()
    }

    viewModel<DetailsVacancyViewModel> {
        DetailsVacancyViewModel(get())
    }
}
