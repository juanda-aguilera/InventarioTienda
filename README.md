# Inventario — Tienda de Don Ernesto (Taller 8, Programación 1)

Aplicación de escritorio en Java Swing con persistencia en MySQL (JDBC, patrón DAO).
Paquete base: `com.gitb.juandaaguilera.inventariotienda`

## Requisitos
- JDK 21
- Maven 3.8+ (o NetBeans con soporte Maven)
- MySQL en `localhost:3306` (XAMPP, Workbench o consola)

## Pasos para ejecutar
1. Clonar el repositorio y abrir la carpeta raíz (donde está `pom.xml`).
2. Iniciar MySQL y ejecutar el script `tienda.sql` (crea la base `tienda`, las tablas y datos de prueba).
3. Si el usuario o la contraseña de MySQL no son `root` / vacío, cambiarlos en
   `src/main/java/com/gitb/juandaaguilera/inventariotienda/conexion/Conexion.java`.
4. Ejecutar la clase `App` desde NetBeans, o en consola: `mvn compile exec:java`
5. En consola debe aparecer `Conexión exitosa` y se abre la ventana del inventario.

## Estructura
- `conexion`: clase `Conexion`
- `model`: `Categoria`, `Producto`
- `dao`: `CategoriaDAO`, `ProductoDAO`
- `vista`: `VentanaInventario` (Swing)
- `App`: clase con `main`