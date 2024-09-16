package ru.practicum.android.diploma.filter.ui.chooseaplaceofwork.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {
    private val _selectedCountry = MutableLiveData<String>()
    val selectedCountry: LiveData<String> = _selectedCountry

    private val _selectedRegion = MutableLiveData<String>()
    val selectedRegion: LiveData<String> = _selectedRegion

    private val _countries = MutableLiveData<List<String>>() // Список стран
    val countries: LiveData<List<String>> get() = _countries

    private val _regions = MutableLiveData<List<String>>() // Список регионов
    val regions: LiveData<List<String>> get() = _regions

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> get() = _errorState

    init {
        loadCountries()
    }

    private fun loadCountries() {
        viewModelScope.launch {
//            val countries = listOf("Russia", "China", "USA")
//            _countries.postValue(countries)
        }
    }

    fun loadRegions(countryId: String) {
        viewModelScope.launch {
//            val regions = when (countryId) {
//                "Russia" -> listOf("Ural", "Moscow", "Siberia")
//                "China" -> listOf("Shanghai", "Beijing", "Guangzhou")
//                "USA" -> listOf("California", "New York", "Texas")
//                else -> emptyList()
//            }
        }
    }

    fun onCountrySelected(country: String) {
        _selectedCountry.value = country
        loadRegions(country)
    }

    fun onRegionSelected(region: String) {
        _selectedRegion.value = region
    }
}
