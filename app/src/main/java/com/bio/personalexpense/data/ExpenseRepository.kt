package com.bio.personalexpense.data

import androidx.lifecycle.LiveData

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()

    fun getExpensesByCategory(category: String): LiveData<List<Expense>> {
        return expenseDao.getExpensesByCategory(category)
    }

    fun getExpensesByDate(date: String): LiveData<List<Expense>> {
        return expenseDao.getExpensesByDate(date)
    }

    fun searchExpenses(query: String): LiveData<List<Expense>> {
        return expenseDao.searchExpenses(query)
    }

    suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }

    fun getTotalAmountByCategory(category: String): LiveData<Double> {
        return expenseDao.getTotalAmountByCategory(category)
    }

    fun getTotalAmountByDate(date: String): LiveData<Double> {
        return expenseDao.getTotalAmountByDate(date)
    }

    fun getTotalAmountByMonth(month: String): LiveData<Double> {
        return expenseDao.getTotalAmountByMonth(month)
    }

    fun getExpensesByMonth(month: String): LiveData<List<Expense>> {
        return expenseDao.getExpensesByMonth(month)
    }

    fun getAvailableMonths(): LiveData<List<String>> {
        return expenseDao.getAvailableMonths()
    }
} 