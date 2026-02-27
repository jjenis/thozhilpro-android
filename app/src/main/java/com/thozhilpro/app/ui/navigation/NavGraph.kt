package com.thozhilpro.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.thozhilpro.app.ui.screens.*

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val CUSTOMERS = "customers"
    const val SUPPLIERS = "suppliers"
    const val ITEMS = "items"
    const val PURCHASES = "purchases"
    const val SALES = "sales"
    const val RETAIL_SALES = "retail_sales"
    const val PAYMENTS = "payments"
    const val EXPENSES = "expenses"
    const val ANALYTICS = "analytics"
    const val NOTIFICATIONS = "notifications"
    const val SETTINGS = "settings"
    const val PROFILE = "profile"
    const val SUBSCRIPTION_EXPIRED = "subscription_expired"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    onLoginSuccess: () -> Unit = {}
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                    onLoginSuccess()
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                    onLoginSuccess()
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(navController = navController)
        }
        composable(Routes.CUSTOMERS) {
            CustomersScreen(navController = navController)
        }
        composable(Routes.SUPPLIERS) {
            SuppliersScreen(navController = navController)
        }
        composable(Routes.ITEMS) {
            ItemsScreen(navController = navController)
        }
        composable(Routes.PURCHASES) {
            PurchasesScreen(navController = navController)
        }
        composable(Routes.SALES) {
            SalesScreen(navController = navController)
        }
        composable(Routes.RETAIL_SALES) {
            RetailSalesScreen(navController = navController)
        }
        composable(Routes.PAYMENTS) {
            PaymentsScreen(navController = navController)
        }
        composable(Routes.EXPENSES) {
            ExpensesScreen(navController = navController)
        }
        composable(Routes.ANALYTICS) {
            AnalyticsScreen(navController = navController)
        }
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(navController = navController)
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(navController = navController)
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                navController = navController,
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.SUBSCRIPTION_EXPIRED) {
            SubscriptionExpiredScreen(
                onSubscriptionRenewed = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.SUBSCRIPTION_EXPIRED) { inclusive = true }
                    }
                }
            )
        }
    }
}
