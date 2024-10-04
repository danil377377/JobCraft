package ru.practicum.android.diploma.search.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.ext.viewBinding
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.search.domain.api.VacancyOnClicked
import ru.practicum.android.diploma.search.presentation.SearchViewModel
import ru.practicum.android.diploma.search.ui.adapter.SearchAdapter

class SearchFragment : Fragment(R.layout.fragment_search) {
    private val binding by viewBinding(FragmentSearchBinding::bind)
    private val viewModel by viewModel<SearchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vacancyOnClicked = object : VacancyOnClicked {
            override fun startVacancy() {
                TODO("Not yet implemented")
            }
        }

        val adapter = SearchAdapter(vacancyOnClicked)
        adapter.submitList(listOf())
        binding.rvFoundVacanciesList.adapter = adapter

        viewModel.getSearchRes().observe(requireActivity()) {
            adapter.submitList(it.vacancies)
        }

        binding.btnFilters.setOnClickListener {
            openFilters()
        }

    }

    private fun openFilters() {
        val action = SearchFragmentDirections.actionSearchFragmentToFiltersFragment()
        findNavController().navigate(action)
    }
}
