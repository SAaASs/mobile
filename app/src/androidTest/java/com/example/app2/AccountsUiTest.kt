package com.example.app2

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(com.example.app2.di.AppModule::class)
class AccountsUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun create_current_account_shows_in_list_and_filter_works() {
        // Create -> Current
        composeRule.onNodeWithTag("createFab").performClick()

        composeRule.onNodeWithTag("numberField").performTextInput("4081780001") // только цифры
        composeRule.onNodeWithTag("chipCurrent").performClick()
        composeRule.onNodeWithTag("serviceFeeField").performTextInput("100")   // только цифры
        composeRule.onNodeWithTag("saveButton").performClick()

        // Вернулись в список
        composeRule.onNodeWithTag("accountCard_4081780001").assertExists()

        // Фильтр по префиксу
        composeRule.onNodeWithTag("filterField").performTextInput("408178")
        composeRule.onNodeWithTag("accountCard_4081780001").assertExists()
    }

    @Test
    fun create_savings_open_details_delete_disappears() {
        // Create -> Savings
        composeRule.onNodeWithTag("createFab").performClick()

        composeRule.onNodeWithTag("chipSavings").performClick()
        composeRule.onNodeWithTag("numberField").performTextInput("5000000001")     // только цифры
        composeRule.onNodeWithTag("interestRateField").performTextInput("7")       // только цифры
        composeRule.onNodeWithTag("capSwitch").performClick()
        composeRule.onNodeWithTag("saveButton").performClick()

        // Открыть details по карточке
        composeRule.onNodeWithTag("accountCard_5000000001").performClick()

        // Удалить
        composeRule.onNodeWithTag("detailsDelete").performClick()

        // Назад в список, карточки нет
        composeRule.onNodeWithTag("accountCard_5000000001").assertDoesNotExist()
    }
}
