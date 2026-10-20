# Inventario Tienda de Don Ernesto (Taller 8, Programación 1)

Juan David Aguilera
Marianna Fernandez 
JeanCarlo Caicedo

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

## Uso de la interfaz gráfica

Al ejecutar `App` se abre la ventana **Inventario — Tienda de Don Ernesto**. Los datos se guardan en MySQL, así que siguen ahí al cerrar y volver a abrir la aplicación.

- **Registrar un producto:** escriba el nombre, el precio y la cantidad, elija la categoría en la lista desplegable y pulse **Guardar**. El producto aparece en la tabla.
- **Consultar:** la tabla muestra todos los productos con su ID, nombre, precio, cantidad y categoría. Para ver solo una categoría, use el filtro "Filtrar por categoría" (la opción "Todas" muestra todo). Los productos con menos de 5 unidades se resaltan en rojo, para saber qué hay que volver a pedir.
- **Actualizar un producto:** haga clic en su fila de la tabla (los datos se cargan en el formulario), modifique lo que necesite y pulse **Actualizar**.
- **Eliminar un producto:** haga clic en su fila, pulse **Eliminar** y confirme en el cuadro de diálogo.
- **Limpiar:** vacía el formulario para empezar un registro nuevo.

Si hay un error (campos vacíos, precio o cantidad que no son números, precio menor o igual a cero, cantidad negativa o fallas de conexión con MySQL), la aplicación muestra un mensaje de error en pantalla.

## Estructura
- `conexion`: clase `Conexion`
- `model`: `Categoria`, `Producto`
- `dao`: `CategoriaDAO`, `ProductoDAO`
- `vista`: `VentanaInventario` (Swing)
- `App`: clase con `main`
