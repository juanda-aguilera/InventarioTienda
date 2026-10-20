package com.gitb.juandaaguilera.inventariotienda.dao;

import com.gitb.juandaaguilera.inventariotienda.conexion.Conexion;
import com.gitb.juandaaguilera.inventariotienda.model.Categoria;
import com.gitb.juandaaguilera.inventariotienda.model.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // JOIN con categoria para construir el objeto Categoria de cada producto
    private static final String SELECT_CON_CATEGORIA
            = "SELECT p.id_producto, p.nombre, p.precio, p.cantidad, "
            + "c.id_categoria, c.nombre AS nombre_categoria "
            + "FROM producto p "
            + "INNER JOIN categoria c ON p.id_categoria = c.id_categoria ";

    public boolean insertar(Producto p) throws SQLException {
        String sql = "INSERT INTO producto (nombre, precio, cantidad, id_categoria) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getCantidad());
            ps.setInt(4, p.getCategoria().getIdCategoria());

            return ps.executeUpdate() > 0;
        }
    }

    public List<Producto> listar() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = SELECT_CON_CATEGORIA + "ORDER BY p.id_producto";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productos.add(mapear(rs));
            }
        }

        return productos;
    }

    public Producto buscarPorId(int id) throws SQLException {
        String sql = SELECT_CON_CATEGORIA + "WHERE p.id_producto = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }

        return null;
    }

    public boolean actualizar(Producto p) throws SQLException {
        String sql = "UPDATE producto SET nombre = ?, precio = ?, cantidad = ?, "
                + "id_categoria = ? WHERE id_producto = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getCantidad());
            ps.setInt(4, p.getCategoria().getIdCategoria());
            ps.setInt(5, p.getIdProducto());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM producto WHERE id_producto = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }

    // Reto opcional (Ejercicio 7)
    public List<Producto> listarPorCategoria(int idCategoria) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = SELECT_CON_CATEGORIA
                + "WHERE c.id_categoria = ? ORDER BY p.id_producto";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapear(rs));
                }
            }
        }

        return productos;
    }

    // Convierte una fila del ResultSet en un Producto con su Categoria
    private Producto mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(
                rs.getInt("id_categoria"),
                rs.getString("nombre_categoria"));

        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre"),
                rs.getDouble("precio"),
                rs.getInt("cantidad"),
                categoria);
    }
}
