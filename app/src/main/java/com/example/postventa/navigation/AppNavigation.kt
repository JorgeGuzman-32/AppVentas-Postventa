package com.example.postventa.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.postventa.ui.LoginScreen
import com.example.postventa.ui.MenuScreen
import com.example.postventa.ui.solicitud.ListaSolicitudesScreen
import com.example.postventa.ui.solicitud.SolicitudDetalleScreen
import com.example.postventa.ui.solicitud.SolicitudFormScreen
import com.example.postventa.ui.solicitud.SolicitudViewModel

@Composable
fun AppNavigation(viewModel: SolicitudViewModel) {
    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = "login") {

        // 1. Pantalla de Login requerida
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("menu") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        // 2. Pantalla de Menú requerida
        composable("menu") {
            MenuScreen(
                onNavigateToSolicitudes = {
                    navController.navigate("lista_solicitudes")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 3. Pantalla de lista (Adaptada al flujo)
        composable("lista_solicitudes") {
            ListaSolicitudesScreen(
                viewModel = viewModel,
                onNuevaSolicitudClick = { navController.navigate("formulario_solicitud") },
                onSolicitudClick = { id -> navController.navigate("detalle_solicitud/$id") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 4. Pantalla de Formulario
        composable("formulario_solicitud") {
            SolicitudFormScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 5. Pantalla de Detalle conservada intacta
        composable(
            route = "detalle_solicitud/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            SolicitudDetalleScreen(
                solicitudId = id,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
