package ru.practicum.android.diploma.filter.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentChoosingAPlaceOfWorkBinding
import ru.practicum.android.diploma.global.util.CustomFragment

class ChoosingAPlaceOfWorkFragment : CustomFragment<FragmentChoosingAPlaceOfWorkBinding>() {

    private val locationViewModel: LocationViewModel by viewModel()
    private var isCountryInputFilled = false
    private var isRegionInputFilled = false

    private var isHaveDataFromFilterSettings = false

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentChoosingAPlaceOfWorkBinding {
        return FragmentChoosingAPlaceOfWorkBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isHaveDataFromFilterSettings = locationViewModel.getDataForCheckHavePlace()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationViewModel.getCountryAndRegion()

        locationViewModel.selectedCountry.observe(viewLifecycleOwner) { country ->
            locationViewModel.setNewCountry(country)
            locationViewModel.comparePlace()
            binding.edCountry.setText(country?.name)
            updateCountryInputUi(country?.name)
        }

        locationViewModel.countryIsChanged.observe(viewLifecycleOwner) { isChanged ->
            if (isChanged) {
                locationViewModel.resetRegion()
            }
        }

        locationViewModel.selectedRegion.observe(viewLifecycleOwner) { region ->
            locationViewModel.setNewRegion(region)
            binding.edRegion.setText(region?.name)
            updateRegionInputUi(region?.name)
            if (locationViewModel.selectedCountry.value == null && region != null) {
                locationViewModel.setCountryFromRegion(region.id)
            }
            locationViewModel.comparePlace()
        }

        locationViewModel.placeIsChanged.observe(viewLifecycleOwner) { isChanged ->
            binding.btChoose.isVisible = isChanged
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!isHaveDataFromFilterSettings) {
                locationViewModel.resetCountry()
            } else {
                locationViewModel.setBackPlaceWork()
            }
            findNavController().popBackStack()
        }

        initClickListener()
    }

    private fun initClickListener() {
        binding.btBackArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.arrowForwardCountry.setOnClickListener {
            findNavController().navigate(R.id.action_choosingAPlaceOfWorkFragment_to_countryFragment)
        }

        binding.edCountry.setOnClickListener {
            findNavController().navigate(R.id.action_choosingAPlaceOfWorkFragment_to_countryFragment)
        }

        binding.arrowForwardRegion.setOnClickListener {
            findNavController().navigate(R.id.action_choosingAPlaceOfWorkFragment_to_areaSelectFragment)
        }

        binding.edRegion.setOnClickListener {
            findNavController().navigate(R.id.action_choosingAPlaceOfWorkFragment_to_areaSelectFragment)
        }

        binding.crossCountry.setOnClickListener {
            binding.edCountry.text = null
            locationViewModel.resetCountry()
        }

        binding.crossRegion.setOnClickListener {
            binding.edRegion.text = null
            locationViewModel.resetRegion()
        }

        binding.btChoose.setOnClickListener {
            locationViewModel.setPlaceWork()
            findNavController().popBackStack()
        }
    }

    private fun updateCountryInputUi(country: String?) {
        isCountryInputFilled = !country.isNullOrEmpty()
        binding.edCountry.isActivated = isCountryInputFilled
        binding.arrowForwardCountry.visibility = if (isCountryInputFilled) View.GONE else View.VISIBLE
        binding.crossCountry.visibility = if (isCountryInputFilled) View.VISIBLE else View.GONE
    }

    private fun updateRegionInputUi(region: String?) {
        isRegionInputFilled = !region.isNullOrEmpty()
        binding.edRegion.isActivated = isRegionInputFilled
        binding.arrowForwardRegion.visibility = if (isRegionInputFilled) View.GONE else View.VISIBLE
        binding.crossRegion.visibility = if (isRegionInputFilled) View.VISIBLE else View.GONE
    }

}
