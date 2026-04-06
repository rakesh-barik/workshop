package com.purina.feedright.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.purina.feedright.data.repository.AuthRepository
import com.purina.feedright.ui.home.HomeScreen
import com.purina.feedright.ui.login.LoginScreen
import com.purina.feedright.ui.visit.VisitRecordScreen

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val RECORD_VISIT = "record_visit"
}

@Composable
fun AppNavigation(
    authRepository: AuthRepository,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val startDestination = if (authRepository.isLoggedIn()) Routes.HOME else Routes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = hiltViewModel(),
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                viewModel = hiltViewModel(),
                onRecordVisit = { navController.navigate(Routes.RECORD_VISIT) },
                onLogout = {
                    authRepository.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.RECORD_VISIT) {
            VisitRecordScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
