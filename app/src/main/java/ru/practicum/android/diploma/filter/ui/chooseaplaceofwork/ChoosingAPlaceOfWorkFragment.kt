package ru.practicum.android.diploma.filter.ui.chooseaplaceofwork

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentChoosingAPlaceOfWorkBinding
import ru.practicum.android.diploma.filter.ui.chooseaplaceofwork.viewmodel.LocationViewModel
import ru.practicum.android.diploma.global.util.CustomFragment

class ChoosingAPlaceOfWorkFragment : CustomFragment<FragmentChoosingAPlaceOfWorkBinding>() {

    private val locationViewModel: LocationViewModel by viewModel()
    private var isCountryInputFilled = false
    private var isRegionInputFilled = false

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentChoosingAPlaceOfWorkBinding {
        return FragmentChoosingAPlaceOfWorkBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.arrowForwardCountry.setOnClickListener {
            val action = ChoosingAPlaceOfWorkFragmentDirections.actionChoosingAPlaceOfWorkFragmentToCountryFragment()
            findNavController().navigate(action)
        }

        binding.arrowForwardRegion.setOnClickListener {
            val action = ChoosingAPlaceOfWorkFragmentDirections.actionChoosingAPlaceOfWorkFragmentToAreaSelectFragment()
            findNavController().navigate(action)
        }

        binding.edCountry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // для detect
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateCountryInputUi(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {
                // для detect
            }
        })

        binding.edRegion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // для detect
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateRegionInputUi(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {
                // для detect
            }
        })

        locationViewModel.selectedCountry.observe(viewLifecycleOwner) { country ->
            binding.edCountry.setText(country)
            binding.edCountry.hint = ""
            binding.tvCountry.visibility = View.VISIBLE
            isCountryInputFilled = true
            updateChooseBtnVisibility()
        }

        locationViewModel.selectedRegion.observe(viewLifecycleOwner) { region ->
            binding.edRegion.setText(region)
            binding.edRegion.hint = ""
            binding.tvRegion.visibility = View.VISIBLE
            isRegionInputFilled = true
            updateChooseBtnVisibility()
        }

        binding.crossCountry.setOnClickListener {
            binding.edCountry.setText("")
        }

        binding.crossRegion.setOnClickListener {
            binding.edRegion.setText("")
        }

        binding.btChoose.setOnClickListener {
            // кнопка выбора-логика подтверждения
        }
    }

    private fun updateCountryInputUi(countryText: String) {
        isCountryInputFilled = countryText.isNotEmpty()
        binding.arrowForwardCountry.visibility = if (isCountryInputFilled) View.GONE else View.VISIBLE
        binding.crossCountry.visibility = if (isCountryInputFilled) View.VISIBLE else View.GONE
        binding.tvCountry.visibility = if (isCountryInputFilled) View.VISIBLE else View.GONE
        updateChooseBtnVisibility()
    }

    private fun updateRegionInputUi(regionText: String) {
        isRegionInputFilled = regionText.isNotEmpty()
        binding.arrowForwardRegion.visibility = if (isRegionInputFilled) View.GONE else View.VISIBLE
        binding.crossRegion.visibility = if (isRegionInputFilled) View.VISIBLE else View.GONE
        binding.tvRegion.visibility = if (isRegionInputFilled) View.VISIBLE else View.GONE
        // updateChooseBtnVisibility()
    }

    private fun updateChooseBtnVisibility() {
        binding.btChoose.visibility = if (isCountryInputFilled && isRegionInputFilled) View.VISIBLE else View.GONE
    }
}
