package com.bio.personalexpense.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bio.personalexpense.R
import com.bio.personalexpense.databinding.FragmentDashboardBinding
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var expenseAdapter: DashboardExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupExpenseRecyclerView()
        setupMonthSpinner()
    }

    private fun setupExpenseRecyclerView() {
        expenseAdapter = DashboardExpenseAdapter()
        binding.categoryBreakdownRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = expenseAdapter
        }
    }

    private fun setupMonthSpinner() {
        viewModel.getAvailableMonths().observe(viewLifecycleOwner) { months ->
            Log.d("DashboardFragment", "Available months: $months")
            if (months.isNotEmpty()) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    months.map { formatMonthString(it) } // Format for display
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.monthSpinner.adapter = adapter

                binding.monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedMonthRaw = months[position] // Get the raw yyyy-MM string
                        observeMonthlyTotal(selectedMonthRaw)
                        observeMonthlyExpenses(selectedMonthRaw)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Do nothing
                    }
                }
                binding.monthSpinner.visibility = View.VISIBLE
                binding.monthlySummaryCard.visibility = View.VISIBLE
                binding.categoryBreakdownLabel.visibility = View.VISIBLE
                binding.categoryBreakdownRecyclerView.visibility = View.VISIBLE
                binding.noMonthsAvailableText.visibility = View.GONE
            } else {
                binding.monthSpinner.visibility = View.GONE
                binding.monthlySummaryCard.visibility = View.GONE
                binding.categoryBreakdownLabel.visibility = View.GONE
                binding.categoryBreakdownRecyclerView.visibility = View.GONE
                binding.noMonthsAvailableText.visibility = View.VISIBLE
                binding.monthlyTotalText.text = "₹ 0.00"
                expenseAdapter.submitList(emptyList())
            }
        }
    }

    private fun observeMonthlyTotal(month: String) {
        viewModel.getTotalAmountByMonth(month).observe(viewLifecycleOwner) { total ->
            val formattedTotal = total?.let {
                val indiaLocale = Locale("en", "IN")
                val numberFormat = NumberFormat.getNumberInstance(indiaLocale)
                numberFormat.minimumFractionDigits = 2
                numberFormat.maximumFractionDigits = 2
                "₹ " + numberFormat.format(it) // Add rupee symbol for dashboard
            } ?: "₹ 0.00"
            binding.monthlyTotalText.text = formattedTotal
        }
    }

    private fun observeMonthlyExpenses(month: String) {
        viewModel.getExpensesByMonth(month).observe(viewLifecycleOwner) { expenses ->
            expenseAdapter.submitList(expenses)
        }
    }

    private fun formatMonthString(monthString: String): String {
        // monthString is in "yyyy-MM" format
        val year = monthString.substring(0, 4)
        val monthNumber = monthString.substring(5, 7).toInt()
        val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        return "${monthNames[monthNumber - 1]} $year"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 