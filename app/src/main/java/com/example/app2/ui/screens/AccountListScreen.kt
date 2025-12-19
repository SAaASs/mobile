package com.example.app2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app2.ui.viewmodel.AccountsViewModel
import com.example.core.format.DefaultAccountFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountListScreen(
    vm: AccountsViewModel,
    onCreate: () -> Unit,
    onOpen: (String) -> Unit,
    onEdit: (String) -> Unit
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val formatter = remember { DefaultAccountFormatter() }

    // ✅ не сбрасывается при повороте
    var filterText by rememberSaveable { mutableStateOf("") }

    // фильтруем по "начинается с" (prefix)
    val filteredAccounts = remember(state.accounts, filterText) {
        val q = filterText.trim()
        if (q.isEmpty()) state.accounts
        else state.accounts.filter { it.number.startsWith(q) }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreate) { Text("+") }
        }
    ) { padd ->
        Column(
            Modifier
                .padding(padd)
                .padding(16.dp)
        ) {
            Text("Счета", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = filterText,
                onValueChange = { filterText = it.filter { ch -> ch.isDigit() } }, // только цифры
                label = { Text("Фильтр по номеру (префикс)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    if (filterText.isNotEmpty()) {
                        TextButton(onClick = { filterText = "" }) { Text("Очистить") }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))

            if (filteredAccounts.isEmpty()) {
                Text("Ничего не найдено")
            } else {
                filteredAccounts.forEach { acc ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(formatter.format(acc))
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = { onOpen(acc.number) }) { Text("Открыть") }
                                TextButton(onClick = { onEdit(acc.number) }) { Text("Изменить") }
                                TextButton(onClick = { vm.delete(acc.number) }) { Text("Удалить") }
                            }
                        }
                    }
                }
            }
        }
    }
}
