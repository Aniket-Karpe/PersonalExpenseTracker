package com.bio.personalexpense.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY date DESC")
    fun getExpensesByDate(date: String): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE title LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%'")
    fun searchExpenses(query: String): LiveData<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT SUM(amount) FROM expenses WHERE category = :category")
    fun getTotalAmountByCategory(category: String): LiveData<Double>

    @Query("SELECT SUM(amount) FROM expenses WHERE date = :date")
    fun getTotalAmountByDate(date: String): LiveData<Double>

    @Query("SELECT SUM(amount) FROM expenses WHERE date LIKE :month || '%'")
    fun getTotalAmountByMonth(month: String): LiveData<Double>

    @Query("SELECT * FROM expenses WHERE date LIKE :month || '%' ORDER BY date DESC")
    fun getExpensesByMonth(month: String): LiveData<List<Expense>>

    @Query("SELECT DISTINCT substr(date, 1, 7) FROM expenses ORDER BY date DESC")
    fun getAvailableMonths(): LiveData<List<String>>
} 