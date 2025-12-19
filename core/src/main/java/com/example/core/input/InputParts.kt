package com.example.core.input


interface InputParser { fun parse(line: String): List<String> }
class SpaceSeparatedParser : InputParser {
    override fun parse(line: String): List<String> =
        line.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
}

interface InputValidator { fun isValid(parts: List<String>): Boolean }
class NonEmptyValidator : InputValidator {
    override fun isValid(parts: List<String>): Boolean = parts.isNotEmpty()
}
