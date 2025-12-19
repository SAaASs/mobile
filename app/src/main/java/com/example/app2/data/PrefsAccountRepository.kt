package com.example.app2.data

import android.content.Context
import com.example.core.model.BankAccount
import com.example.core.model.CurrentAccount
import com.example.core.model.SavingsAccount
import com.example.core.storage.AccountRepository
import com.example.core.storage.InMemoryAccountRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefsAccountRepository @Inject constructor(
    @ApplicationContext context: Context
) : AccountRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val delegate: AccountRepository = InMemoryAccountRepository()

    init {
        loadFromPrefs()
    }

    override fun add(account: BankAccount) =
        delegate.add(account).also { if (it.isSuccess) saveToPrefs() }

    override fun update(number: String, newAccount: BankAccount) =
        delegate.update(number, newAccount).also { if (it.isSuccess) saveToPrefs() }

    override fun delete(number: String): Boolean =
        delegate.delete(number).also { if (it) saveToPrefs() }

    override fun get(number: String): BankAccount? = delegate.get(number)
    override fun getAll(): List<BankAccount> = delegate.getAll()

    private fun loadFromPrefs() {
        val raw = prefs.getString(KEY, null) ?: return
        raw.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .forEach { line -> decode(line)?.let { delegate.add(it) } }
    }

    private fun saveToPrefs() {
        val text = delegate.getAll().joinToString("\n") { encode(it) }
        prefs.edit().putString(KEY, text).apply()
    }

    private fun encode(a: BankAccount): String = when (a) {
        is CurrentAccount -> "T|${a.number}|${a.serviceFee}"
        is SavingsAccount -> "S|${a.number}|${a.interestRate}|${a.capitalization}"
    }

    private fun decode(line: String): BankAccount? {
        val p = line.split("|")
        return when (p.getOrNull(0)) {
            "T" -> if (p.size == 3) CurrentAccount(p[1], p[2].toDoubleOrNull() ?: return null) else null
            "S" -> if (p.size == 4) SavingsAccount(
                p[1],
                p[2].toDoubleOrNull() ?: return null,
                p[3].toBooleanStrictOrNull() ?: return null
            ) else null
            else -> null
        }
    }

    private companion object {
        const val PREFS_NAME = "accounts_prefs"
        const val KEY = "accounts_v1"
    }
}