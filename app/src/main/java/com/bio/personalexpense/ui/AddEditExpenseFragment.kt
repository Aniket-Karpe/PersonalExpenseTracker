package com.bio.personalexpense.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bio.personalexpense.R
import com.bio.personalexpense.data.Expense
import com.bio.personalexpense.databinding.FragmentAddEditExpenseBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEditExpenseFragment : Fragment() {
    private var _binding: FragmentAddEditExpenseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpenseViewModel by viewModels()
    private val args: AddEditExpenseFragmentArgs by navArgs()
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private var currentExpense: Expense? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategoryDropdown()
        setupDatePicker()
        setupSaveButton()
        setupDeleteButton()

        if (args.expenseId != -1L) {
            viewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
                expenses.find { it.id == args.expenseId }?.let { expense ->
                    currentExpense = expense
                    populateFields(expense)
                    binding.deleteButton.visibility = View.VISIBLE
                }
            }
        } else {
            binding.deleteButton.visibility = View.GONE
        }
    }

    private fun setupCategoryDropdown() {
        val categories = arrayOf("Food", "Travel", "Shopping", "Other")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, categories)
        binding.categoryAutoComplete.setAdapter(adapter)
    }

    private fun setupDatePicker() {
        binding.dateEditText.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    binding.dateEditText.setText(dateFormatter.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            if (validateInputs()) {
                saveExpense()
            }
        }
    }

    private fun setupDeleteButton() {
        binding.deleteButton.setOnClickListener {
            currentExpense?.let { expenseToDelete ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Expense")
                    .setMessage("Are you sure you want to delete this expense?")
                    .setPositiveButton("Delete") { dialog, _ ->
                        viewModel.deleteExpense(expenseToDelete)
                        Snackbar.make(binding.root, "Expense deleted", Snackbar.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        with(binding) {
            if (titleEditText.text.isNullOrBlank()) {
                titleLayout.error = "Title is required"
                isValid = false
            } else {
                titleLayout.error = null
            }

            if (amountEditText.text.isNullOrBlank()) {
                amountLayout.error = "Amount is required"
                isValid = false
            } else {
                amountLayout.error = null
            }

            if (categoryAutoComplete.text.isNullOrBlank()) {
                categoryLayout.error = "Category is required"
                isValid = false
            } else {
                categoryLayout.error = null
            }

            if (dateEditText.text.isNullOrBlank()) {
                dateLayout.error = "Date is required"
                isValid = false
            } else {
                dateLayout.error = null
            }
        }

        return isValid
    }

    private fun saveExpense() {
        val expense = Expense(
            id = if (args.expenseId != -1L) args.expenseId else 0,
            title = binding.titleEditText.text.toString(),
            amount = binding.amountEditText.text.toString().toDoubleOrNull() ?: 0.0,
            category = binding.categoryAutoComplete.text.toString(),
            date = binding.dateEditText.text.toString(),
            notes = binding.notesEditText.text.toString().takeIf { it.isNotBlank() }
        )

        if (args.expenseId != -1L) {
            viewModel.updateExpense(expense)
            Snackbar.make(binding.root, "Expense updated", Snackbar.LENGTH_SHORT).show()
        } else {
            viewModel.insertExpense(expense)
            Snackbar.make(binding.root, "Expense added", Snackbar.LENGTH_SHORT).show()
        }

        findNavController().navigateUp()
    }

    private fun populateFields(expense: Expense) {
        with(binding) {
            titleEditText.setText(expense.title)
            amountEditText.setText(expense.amount.toString())
            categoryAutoComplete.setText(expense.category, false)
            dateEditText.setText(expense.date)
            notesEditText.setText(expense.notes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 