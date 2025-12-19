package com.example.app2.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.model.BankAccount
import com.example.core.model.CurrentAccount
import com.example.core.model.SavingsAccount

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val number: String,
    val type: String,                 // "T" или "S"
    val serviceFee: Double?,          // для T
    val interestRate: Double?,        // для S
    val capitalization: Boolean?      // для S
)

fun AccountEntity.toDomain(): BankAccount = when (type) {
    "T" -> CurrentAccount(number = number, serviceFee = serviceFee ?: 0.0)
    "S" -> SavingsAccount(number = number, interestRate = interestRate ?: 0.0, capitalization = capitalization ?: false)
    else -> CurrentAccount(number = number, serviceFee = serviceFee ?: 0.0)
}

fun BankAccount.toEntity(): AccountEntity = when (this) {
    is CurrentAccount -> AccountEntity(
        number = number, type = "T",
        serviceFee = serviceFee, interestRate = null, capitalization = null
    )
    is SavingsAccount -> AccountEntity(
        number = number, type = "S",
        serviceFee = null, interestRate = interestRate, capitalization = capitalization
    )
}