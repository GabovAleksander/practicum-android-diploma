package ru.practicum.android.diploma.favorites.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.search.domain.model.Vacancy
import ru.practicum.android.diploma.vacancy.domain.model.VacancyDetails

interface FavoriteInteractor {
    fun favoriteVacancy(): Flow<List<Vacancy>>

    suspend fun addFavoriteVacancy(vacancyDetails: VacancyDetails)

    suspend fun deleteFavoriteVacancy(vacancyDetails: VacancyDetails)

    fun getFavoriteVacancies(): Flow<List<VacancyDetails>>

    fun getIdsFavoriteVacancies(): Flow<List<String>>
}
