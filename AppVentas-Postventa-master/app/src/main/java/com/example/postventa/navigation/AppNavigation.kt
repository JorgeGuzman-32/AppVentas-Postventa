package com.example.postventa.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

        // 1. Pantalla de Login con selección de perfil
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("menu") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // 2. Pantalla de Menú adaptada al rol
        composable("menu") {
            MenuScreen(
                viewModel = viewModel,
                onNavigateToSolicitudes = {
                    navController.navigate("lista_solicitudes")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 3. Pantalla de lista adaptada al rol (Cliente ve seguimiento, Admin ve control total)
        composable("lista_solicitudes") {
            ListaSolicitudesScreen(
                viewModel = viewModel,
                onNuevaSolicitudClick = { navController.navigate("formulario_solicitud") },
                onSolicitudClick = { id -> navController.navigate("detalle_solicitud/$id") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 4. Pantalla de Formulario (Creación)
        composable("formulario_solicitud") {
            SolicitudFormScreen(
                viewModel = viewModel,
                onGuardarClick = { solicitud ->
                    viewModel.guardarSolicitudEntity(solicitud)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 5. Pantalla de Detalle
        composable(
            route = "detalle_solicitud/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            SolicitudDetalleScreen(
                solicitudId = id,
                viewModel = viewModel,
                onEditarClick = { editId ->
                    navController.navigate("editar_solicitud/$editId")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 6. Pantalla de Formulario (Edición)
        composable(
            route = "editar_solicitud/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val lista by viewModel.listaSolicitudes.collectAsState()
            val solicitud = remember(lista, id) { lista.find { it.id == id } }

            SolicitudFormScreen(
                solicitudExistente = solicitud,
                viewModel = viewModel,
                onGuardarClick = { solicitudActualizada ->
                    viewModel.actualizarSolicitud(solicitudActualizada)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
