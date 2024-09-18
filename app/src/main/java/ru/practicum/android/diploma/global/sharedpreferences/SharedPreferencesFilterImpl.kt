package ru.practicum.android.diploma.global.sharedpreferences

import android.content.SharedPreferences
import com.google.gson.Gson
import ru.practicum.android.diploma.filter.domain.model.FilterStatus

class SharedPreferencesFilterImpl(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson,
) : SharedPreferencesFilter {
    override fun saveFilterState(filterParam: FilterStatus) {
        val json = gson.toJson(filterParam)
        sharedPrefs.edit()
            .putString(FILTER_STATE, json)
            .apply()
    }

    override fun getFilterState(): FilterStatus {
        val json =
            sharedPrefs.getString(
                FILTER_STATE, null
            ) ?: return FilterStatus(null, null, null, null, false)
        return gson.fromJson(json, FilterStatus::class.java)
    }

    companion object {
        const val FILTER_STATE = "filter_state"
    }
}
