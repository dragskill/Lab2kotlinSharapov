package com.example.lab2kotlinsharapov.viewmodel

import androidx.lifecycle.ViewModel
import com.example.lab2kotlinsharapov.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class ExpenseViewModel : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    val totalAmount = expenses.map { list ->
        list.sumOf { expense -> expense.amount }
    }

    private val _showDialogFor = MutableStateFlow<Int?>(null)
    val showDialogFor: StateFlow<Int?> = _showDialogFor

    private var nextId = 1

    fun addExpense(title: String, amountText: String) {
        val amount = amountText.toDoubleOrNull()

        if (title.isBlank() || amount == null || amount <= 0) {
            return
        }

        val newExpense = Expense(
            id = nextId++,
            title = title.trim(),
            amount = amount
        )

        _expenses.update { currentList ->
            currentList + newExpense
        }
    }

    fun requestDelete(expenseId: Int) {
        _showDialogFor.value = expenseId
    }

    fun cancelDelete() {
        _showDialogFor.value = null
    }

    fun confirmDelete() {
        val id = _showDialogFor.value ?: return

        _expenses.update { currentList ->
            currentList.filter { expense -> expense.id != id }
        }

        _showDialogFor.value = null
    }
}