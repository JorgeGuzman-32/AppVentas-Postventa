package com.example.postventa.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    // Estados para controlar los errores requeridos por la pauta
    var usuarioError by remember { mutableStateOf<String?>(null) }
    var contrasenaError by remember { mutableStateOf<String?>(null) }
    var loginGeneralError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo Usuario con Validación Visual
        OutlinedTextField(
            value = usuario,
            onValueChange = {
                usuario = it
                usuarioError = null
                loginGeneralError = null
            },
            label = { Text("Usuario") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            isError = usuarioError != null || loginGeneralError != null,
            supportingText = {
                AnimatedVisibility(visible = usuarioError != null, enter = fadeIn() + expandVertically()) {
                    Text(text = usuarioError ?: "", color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña con Validación Visual
        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                contrasenaError = null
                loginGeneralError = null
            },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = contrasenaError != null || loginGeneralError != null,
            supportingText = {
                AnimatedVisibility(visible = contrasenaError != null, enter = fadeIn() + expandVertically()) {
                    Text(text = contrasenaError ?: "", color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Error general de credenciales incorrectas
        AnimatedVisibility(
            visible = loginGeneralError != null,
            enter = fadeIn() + expandVertically()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = loginGeneralError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Ingresar con lógica de negocio local
        Button(
            onClick = {
                // 1. Validar campos vacíos
                val uEmpty = if (usuario.isBlank()) "El usuario es obligatorio" else null
                val cEmpty = if (contrasena.isBlank()) "La contraseña es obligatoria" else null

                if (uEmpty != null || cEmpty != null) {
                    usuarioError = uEmpty
                    contrasenaError = cEmpty
                    return@Button
                }

                // 2. Validación de credenciales quemadas (Simulación estricta)
                if (usuario == "admin" && contrasena == "duoc2026") {
                    onLoginSuccess()
                } else {
                    loginGeneralError = "Usuario o contraseña incorrectos. Intente con admin / duoc2026"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar a la App")
        }
    }
}


