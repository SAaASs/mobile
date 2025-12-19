package com.example.console


interface InputProvider { fun readLine(): String? }
class ConsoleInputProvider : InputProvider { override fun readLine(): String? = readlnOrNull() }

interface OutputPrinter { fun print(text: String) }
class ConsolePrinter : OutputPrinter { override fun print(text: String) = kotlin.io.println(text) }
