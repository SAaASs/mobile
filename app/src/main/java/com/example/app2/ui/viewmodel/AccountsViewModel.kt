package com.example.app2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app2.data.RoomAccountRepository
import com.example.core.model.BankAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountsUiState(
    val accounts: List<BankAccount> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val repo: RoomAccountRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AccountsUiState())
    val state: StateFlow<AccountsUiState> = _state

    init {
        viewModelScope.launch {
            repo.observeAll().collect { list ->
                _state.update { it.copy(accounts = list) }
            }
        }
    }

    fun add(account: BankAccount) = viewModelScope.launch(Dispatchers.IO) {
        val r = repo.add(account)
        _state.update { it.copy(error = r.exceptionOrNull()?.message) }
    }

    fun update(oldNumber: String, account: BankAccount) = viewModelScope.launch(Dispatchers.IO) {
        val r = repo.update(oldNumber, account)
        _state.update { it.copy(error = r.exceptionOrNull()?.message) }
    }

    fun delete(number: String) = viewModelScope.launch(Dispatchers.IO) {
        repo.delete(number)
    }

    // безопасно для UI: берем из уже загруженного state
    fun findInState(number: String): BankAccount? =
        _state.value.accounts.firstOrNull { it.number == number }
}
