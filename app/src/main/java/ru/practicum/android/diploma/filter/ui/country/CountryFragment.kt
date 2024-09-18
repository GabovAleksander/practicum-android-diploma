package ru.practicum.android.diploma.filter.ui.country

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentCountryBinding
import ru.practicum.android.diploma.filter.domain.model.Country
import ru.practicum.android.diploma.global.util.CustomFragment

class CountryFragment : CustomFragment<FragmentCountryBinding>() {

    private val viewModel by viewModel<CountryViewModel>()
    private val adapter by lazy { CountryAdapter() }
    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCountryBinding {
        return FragmentCountryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        adapter.itemClickListener = { _, item ->
            if (clickDebounce()) {
                viewModel.setCountryInfo(
                    Country(
                        id = item.id, name = item.name
                    )
                )
                findNavController().popBackStack()
            }
        }

        binding.rvCountry.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvCountry.adapter = adapter

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is CountryState.Content -> showContent(state)
                is CountryState.Error -> showPlaceholder()
                is CountryState.Loading -> showLoading()
            }
        }
    }

    private fun showContent(state: CountryState.Content) {
        with(binding) {
            adapter.countryList.addAll(state.region)
            adapter.filteredList.addAll(state.region)
            adapter.notifyDataSetChanged()
            rvCountry.isVisible = true
            progressBar.isVisible = false
            llCountriesNothingFoundPlaceholder.isVisible = false
        }
    }

    private fun showLoading() {
        with(binding) {
            rvCountry.isVisible = false
            progressBar.isVisible = true
            llCountriesNothingFoundPlaceholder.isVisible = false
        }
    }

    private fun showPlaceholder() {
        with(binding) {
            rvCountry.isVisible = false
            progressBar.isVisible = false
            llCountriesNothingFoundPlaceholder.isVisible = true
        }
    }

}
