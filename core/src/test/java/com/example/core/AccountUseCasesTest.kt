package com.example.core

import com.example.core.data.InMemoryAccountsRepository
import com.example.core.domain.usecase.AccountUseCases
import com.example.core.model.CurrentAccount
import com.example.core.model.SavingsAccount
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class AccountUseCasesTest {

    @Test
    fun add_and_observe_returns_added_accounts() = runTest {
        val repo = InMemoryAccountsRepository()
        val useCases = AccountUseCases.from(repo)

        val a1 = CurrentAccount("4081780001", 100.0)
        val a2 = SavingsAccount("4081789999", 7.5, true)

        assertTrue(useCases.add(a1).isSuccess)
        assertTrue(useCases.add(a2).isSuccess)

        val all = useCases.observe().first()
        assertEquals(listOf(a1, a2), all)
    }

    @Test
    fun add_duplicate_number_fails() = runTest {
        val repo = InMemoryAccountsRepository()
        val useCases = AccountUseCases.from(repo)

        val a1 = CurrentAccount("4081780001", 100.0)
        val a2 = CurrentAccount("4081780001", 200.0)

        assertTrue(useCases.add(a1).isSuccess)
        assertTrue(useCases.add(a2).isFailure)
    }

    @Test
    fun update_change_number_and_delete_work() = runTest {
        val repo = InMemoryAccountsRepository()
        val useCases = AccountUseCases.from(repo)

        val a1 = CurrentAccount("4081780001", 100.0)
        assertTrue(useCases.add(a1).isSuccess)

        val updated = CurrentAccount("4081780002", 150.0) // сменили номер
        assertTrue(useCases.update("4081780001", updated).isSuccess)

        val all = useCases.observe().first()
        assertEquals(listOf(updated), all)

        assertTrue(useCases.delete("4081780002"))
        assertTrue(useCases.observe().first().isEmpty())
    }
}
