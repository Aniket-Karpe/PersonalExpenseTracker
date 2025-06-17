package com.bio.personalexpense.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bio.personalexpense.R
import com.bio.personalexpense.data.Expense
import com.bio.personalexpense.databinding.FragmentExpenseListBinding
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExpenseListFragment : Fragment() {
    private var _binding: FragmentExpenseListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var adapter: ExpenseAdapter

    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private var currentFilterCategory: String? = null
    private var currentFilterDate: String? = null
    private var currentSearchQuery: String? = null

    private var allExpensesList: List<Expense> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupChipGroup()
        setupDateFilterButton()
        setupFab()
        observeAllExpenses()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_dashboard -> {
                findNavController().navigate(ExpenseListFragmentDirections.actionExpenseListFragmentToDashboardFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter(
            onItemClick = { expense ->
                navigateToEditExpense(expense.id)
            },
            onItemLongClick = { expense ->
                showDeleteConfirmationDialog(expense)
                true
            }
        )

        binding.expensesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ExpenseListFragment.adapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentSearchQuery = query
                applyFiltersAndSearch()
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchQuery = newText
                applyFiltersAndSearch()
                return true
            }
        })
    }

    private fun setupChipGroup() {
        binding.categoryChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip: Chip? = group.findViewById(checkedId)
            currentFilterCategory = if (chip?.text == "All") null else chip?.text?.toString()
            applyFiltersAndSearch()
        }
        binding.chipAll.isChecked = true
    }

    private fun setupDateFilterButton() {
        binding.dateFilterButton.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    currentFilterDate = dateFormatter.format(calendar.time)
                    applyFiltersAndSearch()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupFab() {
        binding.addExpenseFab.setOnClickListener {
            navigateToAddExpense()
        }
    }

    private fun observeAllExpenses() {
        viewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
            allExpensesList = expenses
            applyFiltersAndSearch()
        }
    }

    private fun applyFiltersAndSearch() {
        var filteredList = allExpensesList

        if (currentFilterCategory != null) {
            filteredList = filteredList.filter { it.category == currentFilterCategory }
        }

        if (currentFilterDate != null) {
            filteredList = filteredList.filter { it.date == currentFilterDate }
        }

        if (currentSearchQuery != null && currentSearchQuery!!.isNotBlank()) {
            filteredList = filteredList.filter {
                it.title.contains(currentSearchQuery!!, ignoreCase = true) ||
                it.notes?.contains(currentSearchQuery!!, ignoreCase = true) == true
            }
        }

        adapter.submitList(filteredList)
        updateTotalSummary(filteredList)
    }

    private fun updateTotalSummary(expenses: List<Expense>) {
        val overallTotal = expenses.sumOf { it.amount }

        val indiaLocale = Locale("en", "IN")
        val numberFormat = NumberFormat.getNumberInstance(indiaLocale)
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2

        binding.todayTotalText.text = numberFormat.format(overallTotal)
    }

    private fun navigateToAddExpense() {
        val action = ExpenseListFragmentDirections
            .actionExpenseListFragmentToAddEditExpenseFragment()
        findNavController().navigate(action)
    }

    private fun navigateToEditExpense(expenseId: Long) {
        val action = ExpenseListFragmentDirections
            .actionExpenseListFragmentToAddEditExpenseFragment(expenseId)
        findNavController().navigate(action)
    }

    private fun showDeleteConfirmationDialog(expense: Expense) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Expense")
            .setMessage("Are you sure you want to delete this expense?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteExpense(expense)
                Snackbar.make(binding.root, "Expense deleted", Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 