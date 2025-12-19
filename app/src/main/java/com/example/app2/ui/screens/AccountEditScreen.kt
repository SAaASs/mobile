package com.example.app2.ui.screens
import androidx.compose.ui.platform.testTag

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.app2.ui.util.UiText
import androidx.compose.ui.unit.dp
import com.example.app2.R
import com.example.core.model.BankAccount
import com.example.core.model.CurrentAccount
import com.example.core.model.SavingsAccount
import com.example.app2.ui.util.asString
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

    var error by rememberSaveable(initKey) { mutableStateOf<UiText?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    TextButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) }
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
                    modifier = Modifier.testTag("chipCurrent"),
                    selected = type == AccountType.CURRENT,
                    onClick = { typeName = AccountType.CURRENT.name },
                    label = { Text(stringResource(R.string.type_current)) }
                )
                FilterChip(
                    modifier = Modifier.testTag("chipSavings"),
                    selected = type == AccountType.SAVINGS,
                    onClick = { typeName = AccountType.SAVINGS.name },
                    label = { Text(stringResource(R.string.type_savings)) }
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(

                value = number,
                onValueChange = { number = it.filter(Char::isDigit).take(19) },
                label = { Text(stringResource(R.string.field_number)) },
                modifier = Modifier.fillMaxWidth().testTag("numberField"),
            )

            Spacer(Modifier.height(12.dp))

            when (type) {
                AccountType.CURRENT -> {
                    OutlinedTextField(
                        value = serviceFee,
                        onValueChange = { serviceFee = it.filter(Char::isDigit).take(19) },
                        label = { Text(stringResource(R.string.field_fee)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("serviceFeeField"),
                    )
                }

                AccountType.SAVINGS -> {
                    OutlinedTextField(
                        value = interestRate,
                        onValueChange = { interestRate = it.filter(Char::isDigit).take(19) },
                        label = { Text(stringResource(R.string.field_rate)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("interestRateField"),
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            modifier = Modifier.testTag("capSwitch"),
                            checked = capitalization,
                            onCheckedChange = { capitalization = it }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.field_capitalization))
                    }
                }
            }

            error?.let {
                Text(it.asString(), color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val n = number.trim()
                    if (n.isEmpty()) {
                        error = UiText.Res(R.string.err_number_empty)
                        return@Button
                    }

                    val account = when (type) {
                        AccountType.CURRENT -> {
                            val fee = serviceFee.replace(',', '.').toDoubleOrNull()
                                ?: run {
                                    error = UiText.Res(R.string.err_fee_invalid)
                                    return@Button
                                }
                            CurrentAccount(n, fee)
                        }

                        AccountType.SAVINGS -> {
                            val rate = interestRate.replace(',', '.').toDoubleOrNull()
                                ?: run {
                                    error = UiText.Res(R.string.err_rate_invalid)
                                    return@Button
                                }
                            SavingsAccount(n, rate, capitalization)
                        }
                    }

                    error = null
                    onSave(account)
                },
                modifier = Modifier.fillMaxWidth().testTag("saveButton"),
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
