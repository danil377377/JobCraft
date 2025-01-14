package ru.practicum.android.diploma.vacancy.ui

import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.model.VacancyDetails
import ru.practicum.android.diploma.common.ext.viewBinding
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.vacancy.presentation.VacancyViewModel
import ru.practicum.android.diploma.vacancy.ui.model.ScreenMode

private const val CORNERRADIUS_DP = 12f

class VacancyFragment : Fragment(R.layout.fragment_vacancy) {
    private val args: VacancyFragmentArgs by navArgs()
    private val viewModel: VacancyViewModel by viewModel { parametersOf(args.vacancyId) }
    private val binding by viewBinding(FragmentVacancyBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        viewModel.getVacancyLiveData().observe(viewLifecycleOwner) { newState ->
            when (newState.screenMode) {
                ScreenMode.LOADING -> showLoading()
                ScreenMode.RESULTS -> showVacancy(newState.vacancy!!)
                else -> showPlaceholder(newState.screenMode)
            }
        }
        viewModel.getIsFavoriteLiveData().observe(viewLifecycleOwner) { newState ->
            changeFavoriteIcon(newState)
        }
        viewModel.setVacancyDetails()
        binding.btnSend.setOnClickListener { viewModel.shareVacancyUrl() }
        binding.btnFavorite.setOnClickListener { viewModel.changeFavorite() }

    }

    private fun showLoading() {
        binding.svVacancy.isVisible = false
        binding.llPlaceholder.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun showVacancy(vacancy: VacancyDetails) {
        binding.svVacancy.isVisible = true
        binding.llPlaceholder.isVisible = false
        binding.progressBar.isVisible = false
        bind(vacancy)
    }

    private fun showPlaceholder(errorMode: ScreenMode) {
        binding.svVacancy.isVisible = false
        binding.llPlaceholder.isVisible = true
        binding.progressBar.isVisible = false
        when (errorMode) {
            ScreenMode.NOTHING_FOUND -> {
                binding.ivVacancyPlaceholder.setImageResource(R.drawable.vacancy_not_found)
                binding.tvVacancyPlaceholder.text = getString(R.string.vacancy_not_found)
            }

            ScreenMode.CONNECTION_PROBLEM -> {
                binding.ivVacancyPlaceholder.setImageResource(R.drawable.er_no_internet)
                binding.tvVacancyPlaceholder.text = getString(R.string.no_internet)
            }

            else -> {
                binding.ivVacancyPlaceholder.setImageResource(R.drawable.server_error)
                binding.tvVacancyPlaceholder.text = getString(R.string.server_error)
            }
        }
    }

    private fun bind(vacancy: VacancyDetails) {
        binding.tvVacancyName.text = vacancy.name
        bindSalary(vacancy.salary)
        Glide.with(requireContext())
            .load(vacancy.employerLogoUrl240)
            .placeholder(R.drawable.ic_cover_placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        CORNERRADIUS_DP,
                        requireContext().resources.displayMetrics
                    ).toInt()
                )
            )
            .into(binding.ivLogo)
        binding.tvEmployerName.text = vacancy.employerName
        binding.tvAddress.text = vacancy.address ?: vacancy.areaName
        bindExperience(vacancy.experience)
        bindScheduleName(vacancy.scheduleName)
        binding.tvDescription.setText(Html.fromHtml(vacancy.description, Html.FROM_HTML_MODE_COMPACT))
        bindKeySkills(vacancy.keySkills)
    }

    private fun bindSalary(salary: String) {
        binding.salary.text = salary
    }

    private fun bindExperience(experience: String?) {
        if (experience == null) {
            binding.tvExperience.isVisible = false
            binding.tvExperienceTitle.isVisible = false
        } else {
            binding.tvExperience.isVisible = true
            binding.tvExperienceTitle.isVisible = true
            binding.tvExperience.text = experience
        }
    }

    private fun bindScheduleName(scheduleName: String?) {
        if (scheduleName == null) {
            binding.tvScheduleName.isVisible = false
        } else {
            binding.tvScheduleName.isVisible = true
            binding.tvScheduleName.setText(Html.fromHtml(scheduleName, Html.FROM_HTML_MODE_COMPACT))
        }
    }

    private fun bindKeySkills(keySkills: List<String>) {
        if (keySkills.isEmpty()) {
            binding.tvKeySkillsTitle.isVisible = false
            binding.tvKeySkills.isVisible = false
        } else {
            binding.tvKeySkillsTitle.isVisible = true
            binding.tvKeySkills.isVisible = true
            binding.tvKeySkills.setText(Html.fromHtml(formattingKeySkills(keySkills), Html.FROM_HTML_MODE_COMPACT))
        }
    }

    private fun formattingKeySkills(keySkills: List<String>): String {
        val builder = StringBuilder()
        for (s: String in keySkills) builder.append("<li> $s</li>")
        return builder.toString()
    }

    private fun changeFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_on)
        } else {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_off)
        }
    }
}
