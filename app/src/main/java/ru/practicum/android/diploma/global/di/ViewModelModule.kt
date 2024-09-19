package ru.practicum.android.diploma.global.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.favorites.ui.viewmodel.FavoriteVacancyFragmentViewModel
import ru.practicum.android.diploma.filter.ui.area.AreaSelectViewModel
import ru.practicum.android.diploma.filter.ui.chooseaplaceofwork.viewmodel.LocationViewModel
import ru.practicum.android.diploma.filter.ui.country.CountryViewModel
import ru.practicum.android.diploma.filter.ui.industry.FilterIndustryViewModel
import ru.practicum.android.diploma.filter.ui.mainfilter.FilterSettingsViewModel
import ru.practicum.android.diploma.search.ui.SearchViewModel
import ru.practicum.android.diploma.vacancy.ui.viewmodel.DetailsVacancyViewModel

val viewModelModule = module {

    viewModel {
        SearchViewModel(searchInteractor = get(), filterInteractor = get())
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

    viewModel<FilterIndustryViewModel> {
        FilterIndustryViewModel(get(), get())
    }

    viewModel<LocationViewModel> {
        LocationViewModel(filterInteractor = get())
    }

    viewModel<AreaSelectViewModel> {
        AreaSelectViewModel(filterInteractor = get(), searchInteractor = get())
    }
    viewModel<FilterSettingsViewModel> {
        FilterSettingsViewModel(get())
    }
    viewModel {
        CountryViewModel(searchInteractor = get(), filterInteractor = get())
    }
}
