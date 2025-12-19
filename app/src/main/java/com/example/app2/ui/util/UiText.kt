package com.example.app2.ui.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {

    data class Res(
        @StringRes val id: Int,
        val args: List<Any> = emptyList()
    ) : UiText

    data class Dynamic(val value: String) : UiText
}

/**
 * Используй в Compose (Text, Snackbar и т.п.)
 */
@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Res -> stringResource(id, *args.toTypedArray())
    is UiText.Dynamic -> value
}

/**
 * Используй вне Compose (Toast, логика Activity и т.п.)
 */
fun UiText.asString(context: Context): String = when (this) {
    is UiText.Res -> context.getString(id, *args.toTypedArray())
    is UiText.Dynamic -> value
}
