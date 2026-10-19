package com.example.postventa.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.postventa.ui.solicitud.ListaSolicitudesScreen
import com.example.postventa.ui.solicitud.SolicitudDetalleScreen
import com.example.postventa.ui.solicitud.SolicitudFormScreen
import com.example.postventa.ui.solicitud.SolicitudViewModel

@Composable
fun AppNavigation(viewModel: SolicitudViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "lista_solicitudes") {
        composable("lista_solicitudes") {
            ListaSolicitudesScreen(
                viewModel = viewModel,
                onNuevaSolicitudClick = { navController.navigate("formulario_solicitud") },
                onSolicitudClick = { id -> navController.navigate("detalle_solicitud/$id") }
            )
        }
        composable("formulario_solicitud") {
            SolicitudFormScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
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