package com.gitb.juandaaguilera.inventariotienda.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centraliza la conexión a MySQL. Todos los DAO piden la conexión aquí.
 */
public class Conexion {

    private static final String URL
            = "jdbc:mysql://localhost:3306/tienda"
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConexion() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            String mensaje = "No se pudo conectar a la base de datos 'tienda'. "
                    + "Verifique que MySQL esté encendido, que la base exista "
                    + "y que el usuario y la contraseña sean correctos. "
                    + "Detalle: " + e.getMessage();
            System.err.println(mensaje);
            // Se relanza con el mensaje claro para que la vista lo muestre en un JOptionPane
            throw new SQLException(mensaje, e);
        }
    }
}
