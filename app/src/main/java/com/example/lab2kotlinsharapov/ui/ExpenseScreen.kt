package com.example.lab2kotlinsharapov.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lab2kotlinsharapov.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel
) {
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())
    val totalAmount by viewModel.totalAmount.collectAsState(initial = 0.0)
    val showDialogFor by viewModel.showDialogFor.collectAsState(initial = null)

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    val amountNumber = amount.toDoubleOrNull()
    val isButtonEnabled = title.isNotBlank() && amountNumber != null && amountNumber > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "💸 Витрати за день")
                        Text(
                            text = "Разом: ₴$totalAmount",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = {
                        Text("Назва")
                    },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = {
                        Text("Сума")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    viewModel.addExpense(title, amount)
                    title = ""
                    amount = ""
                },
                enabled = isButtonEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Додати витрату")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (expenses.isEmpty()) {
                Text(
                    text = "Список витрат порожній",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(expenses) { expense ->
                        ExpenseCard(
                            expense = expense,
                            onLongClick = {
                                viewModel.requestDelete(expense.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialogFor != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.cancelDelete()
            },
            title = {
                Text("Видалення витрати")
            },
            text = {
                Text("Ви дійсно хочете видалити цю витрату?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.confirmDelete()
                    }
                ) {
                    Text("Видалити")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelDelete()
                    }
                ) {
                    Text("Скасувати")
                }
            }
        )
    }
}