package com.example.app2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.unit.dp
import com.example.core.model.BankAccount
import com.example.core.model.CurrentAccount
import com.example.core.model.SavingsAccount

private enum class AccountType { CURRENT, SAVINGS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEditScreen(
    title: String,
    initial: BankAccount?,
    onSave: (BankAccount) -> Unit,
    onCancel: () -> Unit
) {
    // Ключ, чтобы состояние формы было "своё" для Create vs Edit(номер)
    val initKey = initial?.number ?: "NEW"

    val initialTypeName = when (initial) {
        is CurrentAccount -> AccountType.CURRENT.name
        is SavingsAccount -> AccountType.SAVINGS.name
        else -> AccountType.CURRENT.name
    }

    // ✅ Всё, что должно переживать поворот — rememberSaveable
    var typeName by rememberSaveable(initKey) { mutableStateOf(initialTypeName) }
    val type = AccountType.valueOf(typeName)

    var number by rememberSaveable(initKey) { mutableStateOf(initial?.number.orEmpty()) }

    var serviceFee by rememberSaveable(initKey) {
        mutableStateOf((initial as? CurrentAccount)?.serviceFee?.toString().orEmpty())
    }

    var interestRate by rememberSaveable(initKey) {
        mutableStateOf((initial as? SavingsAccount)?.interestRate?.toString().orEmpty())
    }

    var capitalization by rememberSaveable(initKey) {
        mutableStateOf((initial as? SavingsAccount)?.capitalization ?: false)
    }

    var error by rememberSaveable(initKey) { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    TextButton(onClick = onCancel) { Text("Отмена") }
                }
            )
        }
    ) { padd ->
        Column(
            modifier = Modifier
                .padding(padd)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Тип счёта
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = type == AccountType.CURRENT,
                    onClick = { typeName = AccountType.CURRENT.name },
                    label = { Text("Текущий") }
                )
                FilterChip(
                    selected = type == AccountType.SAVINGS,
                    onClick = { typeName = AccountType.SAVINGS.name },
                    label = { Text("Сберегательный") }
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = number,
                onValueChange = { number = it },
                label = { Text("Номер") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            when (type) {
                AccountType.CURRENT -> {
                    OutlinedTextField(
                        value = serviceFee,
                        onValueChange = { serviceFee = it },
                        label = { Text("Плата за обслуживание (руб/мес)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                AccountType.SAVINGS -> {
                    OutlinedTextField(
                        value = interestRate,
                        onValueChange = { interestRate = it },
                        label = { Text("Процентная ставка (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = capitalization,
                            onCheckedChange = { capitalization = it }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Капитализация")
                    }
                }
            }

            error?.let {
                Spacer(Modifier.height(10.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val n = number.trim()
                    if (n.isEmpty()) {
                        error = "Номер не может быть пустым"
                        return@Button
                    }

                    val account = when (type) {
                        AccountType.CURRENT -> {
                            val fee = serviceFee.replace(',', '.').toDoubleOrNull()
                                ?: run {
                                    error = "Некорректная плата за обслуживание"
                                    return@Button
                                }
                            CurrentAccount(n, fee)
                        }

                        AccountType.SAVINGS -> {
                            val rate = interestRate.replace(',', '.').toDoubleOrNull()
                                ?: run {
                                    error = "Некорректная процентная ставка"
                                    return@Button
                                }
                            SavingsAccount(n, rate, capitalization)
                        }
                    }

                    error = null
                    onSave(account)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
        }
    }
}
