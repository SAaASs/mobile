package com.example.core.factory

import com.example.core.model.*

interface AccountFactory {
    fun create(parts: List<String>): BankAccount?
}

class CurrentAccountFactory : AccountFactory {
    override fun create(parts: List<String>): BankAccount? {
        if (parts.size != 3) return null
        val fee = parts[2].toDoubleOrNull() ?: return null
        return CurrentAccount(parts[1], fee)
    }
}

class SavingsAccountFactory : AccountFactory {
    override fun create(parts: List<String>): BankAccount? {
        if (parts.size != 4) return null
        val rate = parts[2].toDoubleOrNull() ?: return null
        val cap = parts[3].toBooleanStrictOrNull() ?: return null
        return SavingsAccount(parts[1], rate, cap)
    }
}

class AccountFactorySelector(
    private val factories: Map<String, AccountFactory>
) {
    fun create(parts: List<String>): BankAccount? {
        val type = parts.firstOrNull() ?: return null
        val factory = factories[type] ?: return null
        return factory.create(parts)
    }
}
