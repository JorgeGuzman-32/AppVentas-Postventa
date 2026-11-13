package com.example.postventa.ui.solicitud

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.postventa.data.local.SolicitudEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaSolicitudesScreen(
    viewModel: SolicitudViewModel,
    onNuevaSolicitudClick: () -> Unit,
    onSolicitudClick: (Long) -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    val perfil by viewModel.perfilUsuario.collectAsState()
    val listaCompleta: List<SolicitudEntity> by viewModel.listaSolicitudes.collectAsState()

    var textoBusqueda by remember { mutableStateOf("") }
    var estadoFiltroSeleccionado by remember { mutableStateOf("Todas") }

    val estadosDisponibles = listOf("Todas", "Recibida", "En revisión", "En proceso", "Resuelta", "Cerrada")

    // REQUISITO CLAVE: Filtrar solicitudes según el rol activo
    val listaSegunPerfil = remember(listaCompleta, perfil) {
        when (perfil) {
            "Cliente" -> listaCompleta.filter { it.clienteId == "CLI-001" } // El cliente ve sus propios ingresos
            "Técnico / Ejecutor" -> listaCompleta.filter { it.estado != "Cerrada" } // El técnico ve las pendientes de resolver / en proceso
            else -> listaCompleta // El administrador ve absolutamente todo
        }
    }

    val listaFiltrada = remember(listaSegunPerfil, textoBusqueda, estadoFiltroSeleccionado) {
        listaSegunPerfil.filter { item ->
            val coincideEstado = estadoFiltroSeleccionado == "Todas" || item.estado.equals(estadoFiltroSeleccionado, ignoreCase = true)
            val coincideTexto = textoBusqueda.isBlank() ||
                    item.titulo.contains(textoBusqueda, ignoreCase = true) ||
                    item.descripcion.contains(textoBusqueda, ignoreCase = true) ||
                    item.categoria.contains(textoBusqueda, ignoreCase = true) ||
                    item.codigoTracking.contains(textoBusqueda, ignoreCase = true)
            coincideEstado && coincideTexto
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (perfil) {
                            "Cliente" -> "Mis Solicitudes y Estado"
                            "Técnico / Ejecutor" -> "Cola de Trabajo Técnico"
                            else -> "Panel Admin - Todas las Solicitudes"
                        }
                    )
                },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            // El cliente o admin pueden registrar nuevas solicitudes
            if (perfil == "Cliente" || perfil == "Administrador / Coordinador") {
                FloatingActionButton(onClick = {
                    viewModel.resetGuardado()
                    onNuevaSolicitudClick()
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Registrar Reclamo")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            DashboardResumenSection(listaSegunPerfil)

            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                placeholder = { Text("Buscar por título, categoría o código...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar") },
                singleLine = true
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estadosDisponibles) { estado ->
                    FilterChip(
                        selected = estadoFiltroSeleccionado == estado,
                        onClick = { estadoFiltroSeleccionado = estado },
                        label = { Text(estado) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (listaFiltrada.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (listaSegunPerfil.isEmpty()) "No hay solicitudes registradas para este perfil." else "No se encontraron solicitudes que coincidan con la búsqueda.",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = listaFiltrada,
                        key = { solicitud: SolicitudEntity -> solicitud.id }
                    ) { item: SolicitudEntity ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically()
                        ) {
                            SolicitudItemCard(
                                item = item,
                                perfilUsuario = perfil,
                                onClick = { onSolicitudClick(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardResumenSection(lista: List<SolicitudEntity>) {
    val total = lista.size
    val recibidas = lista.count { it.estado.equals("Recibida", ignoreCase = true) || it.estado.equals("En revisión", ignoreCase = true) }
    val enProceso = lista.count { it.estado.contains("proceso", ignoreCase = true) }
    val resueltas = lista.count { it.estado.equals("Resuelta", ignoreCase = true) || it.estado.equals("Cerrada", ignoreCase = true) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard("Total", total.toString(), MaterialTheme.colorScheme.primaryContainer, Modifier.weight(1f))
        StatCard("En Revisión", recibidas.toString(), MaterialTheme.colorScheme.secondaryContainer, Modifier.weight(1f))
        StatCard("En Proceso", enProceso.toString(), MaterialTheme.colorScheme.tertiaryContainer, Modifier.weight(1f))
        StatCard("Resueltas", resueltas.toString(), MaterialTheme.colorScheme.surfaceVariant, Modifier.weight(1f))
    }
}

@Composable
fun StatCard(label: String, valor: String, containerColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = valor, style = MaterialTheme.typography.titleLarge)
            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun SolicitudItemCard(
    item: SolicitudEntity,
    perfilUsuario: String,
    onClick: () -> Unit
) {
    val formateador = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val fechaTexto = remember(item.fechaHora) { formateador.format(Date(item.fechaHora)) }

    val colorPrioridad = when (item.prioridad.lowercase()) {
        "urgente" -> Color(0xFFD32F2F)
        "alta" -> Color(0xFFE65100)
        "media" -> Color(0xFF0288D1)
        else -> Color(0xFF2E7D32)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${item.id} - ${item.titulo}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = item.estado, // El cliente ve si está en revisión, en proceso, etc.
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.descripcion, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Categoría: ${item.categoria}", style = MaterialTheme.typography.bodySmall)

                // Solo Técnicos o Administradores ven la Prioridad
                if (perfilUsuario != "Cliente") {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = colorPrioridad.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Prioridad: ${item.prioridad}",
                            color = colorPrioridad,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            if (item.servicio.isNotBlank()) {
                Text(text = "Modalidad: ${item.servicio}", style = MaterialTheme.typography.bodySmall)
            }

            Text(
                text = "Fecha: $fechaTexto",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}
