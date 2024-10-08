package ru.practicum.android.diploma.vacancy.ui.view

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.global.util.CustomFragment
import ru.practicum.android.diploma.global.util.ResponseCodes
import ru.practicum.android.diploma.vacancy.domain.model.VacancyDetails
import ru.practicum.android.diploma.vacancy.ui.adapters.SkillsAdapter
import ru.practicum.android.diploma.vacancy.ui.viewmodel.DetailsVacancyViewModel
import ru.practicum.android.diploma.vacancy.ui.viewmodel.VacancyState

class VacancyFragment : CustomFragment<FragmentVacancyBinding>() {

    private val viewModel by viewModel<DetailsVacancyViewModel>()

    private val adapter by lazy { SkillsAdapter() }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentVacancyBinding {
        return FragmentVacancyBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: VacancyFragmentArgs by navArgs()
        val vacancyId = args.vacancyId

        viewModel.getDetailsVacancy(vacancyId = vacancyId)

        viewModel.getVacancy().observe(viewLifecycleOwner) { state ->
            when (state) {
                is VacancyState.Content -> showContent(state.vacancy)
                is VacancyState.Error -> showError(state.errorCode)
                is VacancyState.Loading -> showLoading()
            }
        }

        viewModel.getFavoritesState().observe(viewLifecycleOwner) { state ->
            setFavoritesState(inFavorite = state)
        }

        binding.icFavorite.setOnClickListener {
            viewModel.addToFavorites()
        }

        binding.icSharing.setOnClickListener {
            viewModel.shareVacancy()
        }

        binding.btBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showLoading() {
        with(binding) {
            progressBar.isVisible = true
            vacancyInfo.isVisible = false
            placeholder.isVisible = false
            icFavorite.isVisible = false
            icSharing.isVisible = false
        }
    }

    private fun showError(errorCode: Int) {
        with(binding) {
            progressBar.isVisible = false
            vacancyInfo.isVisible = false
            icFavorite.isVisible = true
            icSharing.isVisible = true
        }
        when (errorCode) {
            ResponseCodes.CODE_VACANCY_HAVE_NOT -> {
                showPlaceholder(R.drawable.image_no_vacancy, R.string.vacancy_not_found_or_deleted)
            }

            ResponseCodes.CODE_BAD_REQUEST -> {
                showPlaceholder(R.drawable.image_error_server_cat, R.string.server_error)
            }

            ResponseCodes.CODE_NO_CONNECT -> {
                showPlaceholder(R.drawable.image_no_internet, R.string.no_internet)
            }
        }
    }

    private fun showPlaceholder(resIdImg: Int, resIdText: Int) {
        with(binding) {
            placeholder.isVisible = true
            imgPlaceholder.setImageResource(resIdImg)
            txtPlaceholder.text = getString(resIdText)
        }
    }

    private fun showContent(vacancy: VacancyDetails) {
        with(binding) {
            icFavorite.isVisible = true
            icSharing.isVisible = true
            placeholder.isVisible = false
            progressBar.isVisible = false
            vacancyInfo.isVisible = true

            tvNameVacancy.text = vacancy.name
            tvAreaCompany.text = vacancy.area
            setVacancyTextBlocks(vacancy.salary, salary)
            setVacancyTextBlocks(vacancy.employerName, tvNameCompany)

            Glide.with(requireContext())
                .load(vacancy.employerLogo)
                .fitCenter()
                .placeholder(R.drawable.ic_placeholder_32px)
                .into(icCompany)

            if (vacancy.experience != null || vacancy.schedule != null || vacancy.employment != null) {
                experienceContent.text = getString(
                    R.string.experience_employment_schedule,
                    vacancy.experience ?: "",
                    vacancy.employment ?: "",
                    vacancy.schedule ?: ""
                )
            } else {
                experienceContent.isVisible = false
                requiredExperience.isVisible = false
            }

            descriptionContent.setText(Html.fromHtml(vacancy.description, Html.FROM_HTML_MODE_COMPACT))

            setVacancySkills(vacancy.keySkills)

        }
    }

    private fun setVacancySkills(skills: List<String>?) {
        if (skills != null) {
            adapter.skills.addAll(skills)
            binding.keySkillsContent.adapter = adapter
        } else {
            binding.keySkillsContent.isVisible = false
            binding.keySkills.isVisible = false
        }
    }

    private fun setVacancyTextBlocks(text: String?, view: TextView) {
        if (text != null) {
            view.text = text
        } else {
            view.isVisible = false
        }
    }

    private fun setFavoritesState(inFavorite: Boolean) {
        if (inFavorite) {
            binding.icFavorite.setImageResource(R.drawable.ic_favorites_on__24px)
        } else {
            binding.icFavorite.setImageResource(R.drawable.ic_favorites_off__24px)
        }
    }

    override fun onDestroyView() {
        adapter.skills.clear()
        binding.keySkillsContent.adapter = null
        super.onDestroyView()
    }

}
