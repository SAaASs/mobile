package com.example.app2.ui.navigation
import androidx.hilt.navigation.compose.hiltViewModel
//package your.app.ui
import com.example.app2.ui.viewmodel.AccountsViewModel
import com.example.app2.ui.screens.AccountListScreen
import com.example.app2.ui.screens.AccountEditScreen
import com.example.app2.ui.screens.AccountDetailsScreen
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app2.R

object Routes {
    const val LIST = "list"
    const val CREATE = "create"
    const val DETAILS = "details"
    const val EDIT = "edit"
}

@Composable
fun AppNavGraph(vm: AccountsViewModel = hiltViewModel()) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            AccountListScreen(
                vm = vm,
                onCreate = { nav.navigate(Routes.CREATE) },
                onOpen = { number ->
                    nav.navigate("${Routes.DETAILS}/${Uri.encode(number)}")
                },
                onEdit = { number ->
                    nav.navigate("${Routes.EDIT}/${Uri.encode(number)}")
                }
            )
        }

        composable(Routes.CREATE) {
            AccountEditScreen(
                title = stringResource(R.string.creating),
                initial = null,
                onSave = { acc ->
                    vm.add(acc)
                    nav.popBackStack()
                },
                onCancel = { nav.popBackStack() }
            )
        }

        composable(
            route = "${Routes.DETAILS}/{number}",
            arguments = listOf(navArgument("number") { type = NavType.StringType })
        ) { backStack ->
            val number = backStack.arguments?.getString("number") ?: return@composable
            AccountDetailsScreen(
                vm = vm,
                number = number,
                onBack = { nav.popBackStack() },
                onEdit = { nav.navigate("${Routes.EDIT}/${Uri.encode(number)}") }
            )
        }

        composable(
            route = "${Routes.EDIT}/{number}",
            arguments = listOf(navArgument("number") { type = NavType.StringType })
        ) { backStack ->
            val number = backStack.arguments?.getString("number") ?: return@composable
            val acc = vm.findInState(number)


            AccountEditScreen(
                title = stringResource(R.string.editing),
                initial = acc,
                onSave = { newAcc ->
                    vm.update(number, newAcc)
                    nav.popBackStack()
                },
                onCancel = { nav.popBackStack() }
            )
        }
    }
}
