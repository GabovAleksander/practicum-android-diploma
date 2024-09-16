package ru.practicum.android.diploma.global.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.favorites.ui.viewmodel.FavoriteVacancyFragmentViewModel
import ru.practicum.android.diploma.filter.ui.area.AreaSelectViewModel
import ru.practicum.android.diploma.filter.ui.mainfilter.FilterSettingsViewModel
import ru.practicum.android.diploma.filter.ui.chooseaplaceofwork.viewmodel.LocationViewModel
import ru.practicum.android.diploma.search.ui.SearchViewModel
import ru.practicum.android.diploma.vacancy.ui.viewmodel.DetailsVacancyViewModel
import ru.practicum.android.diploma.filter.ui.country.CountryViewModel

val viewModelModule = module {

    viewModel {
        SearchViewModel(searchInteractor = get())
    }

    viewModel<DetailsVacancyViewModel> {
        DetailsVacancyViewModel(
            searchInteractor = get(),
            favoriteInteractor = get(),
            sharingInteractor = get()
        )
    }

    viewModel<FavoriteVacancyFragmentViewModel> {
        FavoriteVacancyFragmentViewModel(favoriteInteractor = get())
    }

    viewModel<LocationViewModel> {
        LocationViewModel()
    }
    viewModel<AreaSelectViewModel> {
        AreaSelectViewModel(filterInteractor = get())
    }
    viewModel<FilterSettingsViewModel> {
        FilterSettingsViewModel()
    }
    viewModel {
        CountryViewModel(get())
    }
}
