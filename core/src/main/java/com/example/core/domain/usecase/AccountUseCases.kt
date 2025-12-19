package com.example.core.domain.usecase


import com.example.core.domain.repository.AccountsRepository

data class AccountUseCases(
    val observe: ObserveAccountsUseCase,
    val add: AddAccountUseCase,
    val update: UpdateAccountUseCase,
    val delete: DeleteAccountUseCase
) {
    companion object {
        fun from(repo: AccountsRepository) = AccountUseCases(
            observe = ObserveAccountsUseCase(repo),
            add = AddAccountUseCase(repo),
            update = UpdateAccountUseCase(repo),
            delete = DeleteAccountUseCase(repo)
        )
    }
}
