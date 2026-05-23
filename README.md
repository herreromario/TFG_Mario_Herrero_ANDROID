# StockMeal

StockMeal es una aplicación Android desarrollada con Kotlin y Jetpack Compose para gestionar recetas, productos y producción de alimentos. La app utiliza una arquitectura basada en repositorios y consume un backend REST usando Retrofit con JSON.

## Descripción

- App de inventario y gestión de recetas.
- Interfaz construida con Jetpack Compose y Material 3.
- Conexión a un servicio REST backend para productos, recetas y producción.
- Navegación interna usando Navigation Compose.
- Compatible con Android 24+.

## Características principales

- Pantalla de stock de productos.
- Historial de producción.
- Detalle de recetas.
- Registro de producción con cálculo de capacidad.
- Estado de carga y manejo de errores.
- Consumo de APIs remotas con Retrofit y Kotlinx Serialization.

## Pantallas y casos de uso

- **Dashboard**: la pantalla principal muestra la producción del día, permite ver el historial pasado, registrar una nueva producción y acceder a alertas de stock.
- **Recetas**: lista los platos disponibles con información de cuánto se pudo producir recientemente. Desde aquí el usuario puede tocar una receta para abrir su detalle completo.
- **Detalle de receta**: muestra los ingredientes necesarios, cantidad requerida por unidad y el stock actual. Ideal para planificar producción y verificar si hay insumos suficientes.
- **Stock**: vista de inventario de ingredientes, con barra de progreso de stock y un filtro para mostrar solo alertas. Permite ver rápidamente qué productos están por debajo del mínimo y requieren atención.
- **Registrar producción**: flujo para seleccionar un plato, ver la capacidad máxima de producción según ingredientes disponibles, ajustar la cantidad y registrar la nueva producción.
- **Histórico de producción**: presenta registros anteriores agrupados por fecha, lo que facilita revisar qué se produjo en días pasados y comparar tendencias.

### Casos de uso típicos

- Planificar qué platos preparar hoy según el stock disponible.
- Identificar ingredientes en alerta antes de que falten por completo.
- Registrar producción en tiempo real con validación de capacidad.
- Consultar recetas y verificar si los ingredientes están disponibles.
- Revisar histórico para analizar la producción pasada.

## Tecnologías

- Kotlin
- Android Jetpack Compose
- Material 3
- Retrofit
- Kotlinx Serialization
- Navigation Compose
- Room (para persistencia local)
- KSP

## Configuración del proyecto

- `applicationId`: `com.example.stockmeal`
- `compileSdk`: `36`
- `targetSdk`: `36`
- `minSdk`: `24`
- `versionCode`: `1`
- `versionName`: `1.0`

## Requisitos

- JDK 17+ compatible
- Android Studio Bumblebee o superior
- Gradle Wrapper incluido en el proyecto
- Emulador o dispositivo Android con API 24+

## Backend

La app se conecta a un backend en:

```text
http://10.0.2.2:8080/
```

Para ejecutar la app en un emulador de Android, asegúrate de tener el servidor backend disponible en esa dirección. Si usas un dispositivo físico, actualiza la URL del backend según corresponda.

## Cómo compilar y ejecutar

Desde la raíz del proyecto en Windows:

```powershell
.\gradlew.bat clean assembleDebug
.\gradlew.bat installDebug
```

También puedes abrir el proyecto en Android Studio y ejecutar la configuración `app` directamente.

## Pruebas

```powershell
.\gradlew.bat test
```

```powershell
.\gradlew.bat connectedAndroidTest
```

## Estructura del proyecto

- `app/`: módulo Android principal.
- `app/src/main/java/com/example/stockmeal`: código fuente.
- `app/src/main/res`: recursos de Android.
- `gradle/libs.versions.toml`: versiones de dependencias.
- `settings.gradle.kts`: configuración de módulos.

## Notas

- La app está diseñada para ser ejecutada con un backend local o remoto.
- Si la API cambia, actualiza `app/src/main/java/com/example/stockmeal/datos/ContenedorStockMeal.kt` con la nueva `baseUrl`.

---