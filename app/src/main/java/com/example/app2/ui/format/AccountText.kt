package com.example.app2.ui.format

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.app2.R
import com.example.core.model.BankAccount
import com.example.core.model.CurrentAccount
import com.example.core.model.SavingsAccount

@Composable
fun accountText(account: BankAccount): String = when (account) {
    is CurrentAccount ->
        stringResource(R.string.fmt_current_account, account.number, account.serviceFee)

    is SavingsAccount -> {
        val cap = stringResource(if (account.capitalization) R.string.cap_with else R.string.cap_without)
        stringResource(R.string.fmt_savings_account, account.number, account.interestRate, cap)
    }
}
