package com.example.app2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.usecase.AccountUseCases
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
    private val useCases: AccountUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(AccountsUiState())
    val state: StateFlow<AccountsUiState> = _state

    init {
        // список сам обновляется из репозитория (Room -> Flow)
        viewModelScope.launch {
            useCases.observe().collect { list ->
                _state.update { it.copy(accounts = list) }
            }
        }
    }

    fun add(account: BankAccount) = viewModelScope.launch(Dispatchers.IO) {
        val r = useCases.add(account)
        _state.update { it.copy(error = r.exceptionOrNull()?.message) }
    }

    fun update(oldNumber: String, account: BankAccount) = viewModelScope.launch(Dispatchers.IO) {
        val r = useCases.update(oldNumber, account)
        _state.update { it.copy(error = r.exceptionOrNull()?.message) }
    }

    fun delete(number: String) = viewModelScope.launch(Dispatchers.IO) {
        useCases.delete(number)
    }

    // удобно для экранов Details/Edit: берём из уже загруженного списка
    fun findInState(number: String): BankAccount? =
        _state.value.accounts.firstOrNull { it.number == number }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
