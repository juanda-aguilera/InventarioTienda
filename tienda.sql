-- Taller 8: Inventario de la tienda de Don Ernesto
CREATE DATABASE IF NOT EXISTS tienda;
USE tienda;

CREATE TABLE IF NOT EXISTS categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    cantidad INT NOT NULL DEFAULT 0,
    id_categoria INT NOT NULL,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

INSERT IGNORE INTO categoria (nombre)
VALUES ('Lacteos'), ('Aseo'), ('Bebidas'), ('Granos');

-- Productos de prueba
INSERT INTO producto (nombre, precio, cantidad, id_categoria)
VALUES ('Leche entera', 4500, 20, (SELECT id_categoria FROM categoria WHERE nombre = 'Lacteos'));
INSERT INTO producto (nombre, precio, cantidad, id_categoria)
VALUES ('Jabon rey', 2800, 15, (SELECT id_categoria FROM categoria WHERE nombre = 'Aseo'));
INSERT INTO producto (nombre, precio, cantidad, id_categoria)
VALUES ('Arroz 500 g', 2600, 30, (SELECT id_categoria FROM categoria WHERE nombre = 'Granos'));

-- Verificación
SELECT p.id_producto, p.nombre, p.precio, p.cantidad, c.nombre AS categoria
FROM producto p
JOIN categoria c ON p.id_categoria = c.id_categoria;

-- Ejercicio 6c (ejecutar a mano para observar el error de llave foránea):
-- DELETE FROM categoria WHERE nombre = 'Lacteos';
