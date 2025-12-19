package com.example.console


import com.example.core.factory.*
import com.example.core.format.DefaultAccountFormatter
import com.example.core.input.*
import com.example.core.storage.InMemoryAccountRepository

fun main() {
    val repo = InMemoryAccountRepository()

    val selector = AccountFactorySelector(
        mapOf("T" to CurrentAccountFactory(), "S" to SavingsAccountFactory())
    )

    val inputProcessor = AccountInputProcessor(
        input = ConsoleInputProvider(),
        parser = SpaceSeparatedParser(),
        validator = NonEmptyValidator(),
        factorySelector = selector,
        repo = repo
    )

    val formatter = DefaultAccountFormatter()
    val printer = ConsolePrinter()

    inputProcessor.processInput()

    printer.print("\nВсе счета:")
    repo.getAll().forEach { printer.print(formatter.format(it)) }
}
