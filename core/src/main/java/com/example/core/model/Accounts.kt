package com.example.core.model


sealed interface BankAccount {
    val number: String
}

data class CurrentAccount(
    override val number: String,
    val serviceFee: Double
) : BankAccount

data class SavingsAccount(
    override val number: String,
    val interestRate: Double,
    val capitalization: Boolean
) : BankAccount
