@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.app2.ui.screens



import com.example.core.format.*
import androidx.compose.runtime.Composable
import com.example.app2.ui.viewmodel.AccountsViewModel

import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun AccountDetailsScreen(
    vm: AccountsViewModel,
    number: String,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val formatter = remember { DefaultAccountFormatter() }
    val acc = vm.findInState(number)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Просмотр") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Назад") } }
            )
        }
    ) { padd ->
        Column(Modifier.padding(padd).padding(16.dp)) {
            if (acc == null) {
                Text("Счёт не найден")
                return@Column
            }
            Text(formatter.format(acc))
            Spacer(Modifier.height(12.dp))
            Row {
                Button(onClick = onEdit) { Text("Редактировать") }
                Spacer(Modifier.width(12.dp))
                OutlinedButton(onClick = { vm.delete(number); onBack() }) { Text("Удалить") }
            }
        }
    }
}
