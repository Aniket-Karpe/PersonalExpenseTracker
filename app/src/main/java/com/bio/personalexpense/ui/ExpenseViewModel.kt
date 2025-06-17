package com.bio.personalexpense.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.bio.personalexpense.data.Expense
import com.bio.personalexpense.data.ExpenseDatabase
import com.bio.personalexpense.data.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExpenseRepository
    val allExpenses: LiveData<List<Expense>>

    init {
        val expenseDao = ExpenseDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(expenseDao)
        allExpenses = repository.allExpenses
    }

    fun insertExpense(expense: Expense) = viewModelScope.launch {
        repository.insertExpense(expense)
    }

    fun updateExpense(expense: Expense) = viewModelScope.launch {
        repository.updateExpense(expense)
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        repository.deleteExpense(expense)
    }

    fun getExpensesByCategory(category: String): LiveData<List<Expense>> {
        return repository.getExpensesByCategory(category)
    }

    fun getExpensesByDate(date: String): LiveData<List<Expense>> {
        return repository.getExpensesByDate(date)
    }

    fun searchExpenses(query: String): LiveData<List<Expense>> {
        return repository.searchExpenses(query)
    }

    fun getTotalAmountByCategory(category: String): LiveData<Double> {
        return repository.getTotalAmountByCategory(category)
    }

    fun getTotalAmountByDate(date: String): LiveData<Double> {
        return repository.getTotalAmountByDate(date)
    }

    fun getTotalAmountByMonth(month: String): LiveData<Double> {
        return repository.getTotalAmountByMonth(month)
    }

    fun getExpensesByMonth(month: String): LiveData<List<Expense>> {
        return repository.getExpensesByMonth(month)
    }

    fun getAvailableMonths(): LiveData<List<String>> {
        return repository.getAvailableMonths()
    }
} 