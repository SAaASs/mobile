package com.example.app2.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app2.R
import com.example.app2.ui.format.accountText
import com.example.app2.ui.viewmodel.AccountsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountListScreen(
    vm: AccountsViewModel,
    onCreate: () -> Unit,
    onOpen: (String) -> Unit,
    onEdit: (String) -> Unit
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var filterText by rememberSaveable { mutableStateOf("") }

    val filteredAccounts = remember(state.accounts, filterText) {
        val q = filterText.trim()
        if (q.isEmpty()) state.accounts
        else state.accounts.filter { it.number.startsWith(q) }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.testTag("createFab"),
                onClick = onCreate
            ) { Text("+") }
        }
    ) { padd ->
        Column(
            Modifier
                .padding(padd)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(stringResource(R.string.title_accounts))

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = filterText,
                onValueChange = { filterText = it.filter(Char::isDigit).take(19) },
                label = { Text(stringResource(R.string.filter_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    if (filterText.isNotEmpty()) {
                        TextButton(onClick = { filterText = "" }) {
                            Text(stringResource(R.string.clear))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("filterField"),
            )

            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))

            // ✅ Скроллящийся список
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // занимает оставшееся место и дает скролл
                contentPadding = PaddingValues(bottom = 88.dp) // чтобы низ не прятался под FAB
            ) {
                if (filteredAccounts.isEmpty()) {
                    item {
                        Text(stringResource(R.string.nothing_found))
                    }
                } else {
                    items(
                        items = filteredAccounts,
                        key = { it.number }
                    ) { acc ->
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .testTag("accountCard_${acc.number}")
                                .clickable { onOpen(acc.number) }
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(accountText(acc))

                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = { onOpen(acc.number) }) {
                                        Text(stringResource(R.string.open))
                                    }
                                    TextButton(onClick = { onEdit(acc.number) }) {
                                        Text(stringResource(R.string.edit))
                                    }
                                    TextButton(onClick = { vm.delete(acc.number) }) {
                                        Text(stringResource(R.string.delete))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
