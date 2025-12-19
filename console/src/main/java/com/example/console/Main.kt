package com.example.console

import com.example.core.data.InMemoryAccountsRepository
import com.example.core.domain.usecase.AccountUseCases
import com.example.core.factory.*
import com.example.core.format.DefaultAccountFormatter
import com.example.core.input.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val repo = InMemoryAccountsRepository()
    val useCases = AccountUseCases.from(repo)

    val selector = AccountFactorySelector(
        mapOf("T" to CurrentAccountFactory(), "S" to SavingsAccountFactory())
    )

    val inputProcessor = AccountInputProcessor(
        input = ConsoleInputProvider(),
        parser = SpaceSeparatedParser(),
        validator = NonEmptyValidator(),
        factorySelector = selector,
        useCases = useCases
    )

    val formatter = DefaultAccountFormatter()
    val printer = ConsolePrinter()

    // ✅ Сначала вводим данные
    inputProcessor.processInput()

    // ✅ Потом получаем "текущий" список (StateFlow отдаст актуальное)
    val accounts = useCases.observe().first()

    printer.print("\nВсе счета:")
    accounts.forEach { printer.print(formatter.format(it)) }
}
