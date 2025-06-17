package com.bio.personalexpense.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bio.personalexpense.data.Expense
import com.bio.personalexpense.databinding.ItemDashboardExpenseBinding
import java.text.NumberFormat
import java.util.Locale

class DashboardExpenseAdapter : ListAdapter<Expense, DashboardExpenseAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemDashboardExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExpenseViewHolder(
        private val binding: ItemDashboardExpenseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            binding.apply {
                expenseTitle.text = expense.title
                expenseCategory.text = expense.category
                expenseDate.text = expense.date

                val indiaLocale = Locale("en", "IN")
                val numberFormat = NumberFormat.getNumberInstance(indiaLocale)
                numberFormat.minimumFractionDigits = 2
                numberFormat.maximumFractionDigits = 2
                expenseAmount.text = "₹ " + numberFormat.format(expense.amount)
            }
        }
    }

    private class ExpenseDiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem == newItem
        }
    }
} 