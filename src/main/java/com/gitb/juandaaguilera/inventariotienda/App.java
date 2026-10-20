package com.gitb.juandaaguilera.inventariotienda;

import com.gitb.juandaaguilera.inventariotienda.conexion.Conexion;
import com.gitb.juandaaguilera.inventariotienda.vista.VentanaInventario;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {

    public static void main(String[] args) {
        // Prueba de conexión antes de abrir la ventana
        try (Connection con = Conexion.getConexion()) {
            System.out.println("Conexión exitosa");
        } catch (SQLException e) {
            // El mensaje claro ya se imprimió en Conexion; la ventana también avisará con JOptionPane
            System.err.println("Fallo la prueba de conexión.");
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, se usa el aspecto por defecto
        }

        SwingUtilities.invokeLater(() -> new VentanaInventario().setVisible(true));
    }
}
