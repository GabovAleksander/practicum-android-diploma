package ru.practicum.android.diploma.filter.ui.country

import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.CountryItemBinding
import ru.practicum.android.diploma.filter.domain.model.Country

class CountryViewHolder(private val binding: CountryItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(country: Country) {
        binding.tvCountry.text = country.name
    }
}
