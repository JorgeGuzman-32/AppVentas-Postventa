# App de Gestión de Postventa y Reclamos (DSY1105)

Aplicación móvil desarrollada en Android Studio con Jetpack Compose, arquitectura MVVM y persistencia local mediante Room Database para la Evaluación Parcial 2.

## Integrantes del Equipo
* **Estudiante 1:** [Jorge Guzman] - GitHub: [usuario1]
* **Estudiante 2:** [Francisco Ordenes] - GitHub: [usuario2]

## Problemática Resuelta
Optimización de la captura, trazabilidad y almacenamiento local offline de solicitudes de postventa, garantías y reclamos para retail.

## Características Técnicas Implementadas
* **Interfaz de Usuario:** Jetpack Compose con Material Design 3 y navegación desacoplada.
* **Formularios con Validación:** Validación reactiva por campo (`isError`, `supportingText`, íconos de alerta) gestionada en ViewModel.
* **Gestión de Estado:** Manejo inmutable con `UiState` y `StateFlow`.
* **Persistencia Local Real:** SQLite estructurado con Room Database (`@Entity`, `@Dao`, `AppDatabase`).
* **Recursos Nativos:**
    1. *Cámara y Galería:* Captura directa y selección múltiple de fotos para evidencia de fallas.
    2. *Vibración Háptica (`Vibrator`):* Retroalimentación física ante error de validación o guardado exitoso.
* **Animaciones:** Transición fluida en lista (`slideInVertically` + `fadeIn`) y despliegue animado de mensajes de error (`expandVertically`).
* **Vista Detallada:** Previsualización ampliada de evidencias e integración con visores del sistema mediante Intent.

## Planificación y Colaboración
* Tablero Kanban en Trello: (https://trello.com/invite/b/6ad6553165e85e01c7f5e895/ATTId9827e2648e19653d39714fa2c450786E4ACF28C/control)
* Historial de commits técnicos distribuidos entre ambos integrantes.

## Instrucciones para Ejecutar
1. Clonar el repositorio: `git clone https://github.com/JorgeGuzman-32/AppVentas-Postventa.git`
2. Abrir la carpeta raíz en Android Studio.
3. Sincronizar Gradle (`Sync Project with Gradle Files`).
4. Seleccionar un emulador con API 26 o superior (o dispositivo físico).
5. Compilar y ejecutar (`Run 'app'`).