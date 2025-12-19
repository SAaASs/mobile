package com.example.core.format


import com.example.core.model.*

interface AccountFormatter {
    fun format(account: BankAccount): String
}

class DefaultAccountFormatter : AccountFormatter {
    override fun format(account: BankAccount): String = when (account) {
        is CurrentAccount ->
            "Текущий счёт №${account.number}, обслуживание: ${account.serviceFee} руб/мес"
        is SavingsAccount ->
            "Сберегательный счёт №${account.number}, процент: ${account.interestRate}%, " +
                    (if (account.capitalization) "с капитализацией" else "без капитализации")
    }
}
