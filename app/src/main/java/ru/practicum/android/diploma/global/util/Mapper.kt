package ru.practicum.android.diploma.global.util

import android.content.Context
import android.util.TypedValue
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.filter.domain.model.Country
import ru.practicum.android.diploma.filter.domain.model.Location
import ru.practicum.android.diploma.global.util.Constants.NUMBER_1
import ru.practicum.android.diploma.global.util.Constants.NUMBER_10
import ru.practicum.android.diploma.global.util.Constants.NUMBER_2
import ru.practicum.android.diploma.global.util.Constants.NUMBER_4
import ru.practicum.android.diploma.search.data.dto.SalaryDto
import ru.practicum.android.diploma.search.data.dto.details.KeySkillsDto
import java.text.NumberFormat
import java.util.Locale

object Mapper {

    fun mapRadiusForGlide(context: Context, radius: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            radius,
            context.resources.displayMetrics
        ).toInt()
    }

    fun declineVacancy(context: Context, num: Int): String {
        if (num % Constants.NUMBER_100 / NUMBER_10 == NUMBER_1) {
            return context.getString(R.string.decline_vacancy_11)
        }
        return when (num % NUMBER_10) {
            NUMBER_1 -> context.getString(R.string.decline_vacancy_1)
            in NUMBER_2..NUMBER_4 -> context.getString(R.string.decline_vacancy_2_4)
            else -> context.getString(R.string.decline_vacancy_default)
        }
    }

    fun mapSalaryToString(salary: SalaryDto?): String {
        val currencySymbol = when (salary?.currency) {
            "RUB", "RUR" -> "₽"
            "BYR" -> "Br"
            "USD" -> "$"
            "EUR" -> "€"
            "KZT" -> "₸"
            "UAH" -> "₴"
            "AZN" -> "₼"
            "UZS" -> "сум"
            "GEL" -> "₾"
            "KGT" -> "сом"
            else -> ""
        }

        val from = formatNumber(salary?.from)
        val to = formatNumber(salary?.to)

        return when {
            from != null && to != null -> "От $from до $to $currencySymbol"
            from != null -> "От $from $currencySymbol"
            to != null -> "До $to $currencySymbol"
            else -> "Зарплата не указана"
        }
    }

    private fun formatNumber(value: Int?): String? {
        return value?.let {
            val format = NumberFormat.getNumberInstance(Locale("ru", "RU"))
            format.format(it)
        }
    }

    fun mapSkillsToStringList(list: List<KeySkillsDto>?): List<String>? {
        return if (list?.isEmpty() == true) {
            null
        } else {
            list?.map { it.name }
        }
    }

    fun getAreasByCountry(locations: List<Location>, countryId: String): List<Location> {
        return locations
            .filter {
                it.country.id == countryId
            }
    }

    fun getCountryByAreaId(areaId: String, locations: List<Location>): Country? {
        val location = locations.find { it.area.id == areaId }
        return location?.country
    }

}
