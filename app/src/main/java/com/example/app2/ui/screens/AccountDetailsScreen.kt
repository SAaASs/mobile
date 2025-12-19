@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.app2.ui.screens



import com.example.core.format.*
import androidx.compose.runtime.Composable
import com.example.app2.ui.viewmodel.AccountsViewModel
import androidx.compose.ui.platform.testTag
import com.example.app2.ui.format.accountText
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.app2.R

@Composable
fun AccountDetailsScreen(
    vm: AccountsViewModel,
    number: String,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val acc = vm.findInState(number)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.watch)) },
                navigationIcon = { TextButton(onClick = onBack) { Text(stringResource(R.string.back)) } }
            )
        }
    ) { padd ->
        Column(Modifier.padding(padd).padding(16.dp)) {
            if (acc == null) {
                Text(stringResource(R.string.not_found))
                return@Column
            }
            Text(accountText(acc))

            Spacer(Modifier.height(12.dp))
            Row {
                Button(modifier = Modifier.testTag("detailsEdit"),onClick = onEdit) { Text(stringResource(R.string.edit2)) }
                Spacer(Modifier.width(12.dp))
                OutlinedButton(modifier = Modifier.testTag("detailsDelete"),onClick = { vm.delete(number); onBack() }) { Text(stringResource(R.string.delete)) }
            }
        }
    }
}
