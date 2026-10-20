package com.gitb.juandaaguilera.inventariotienda.vista;

import com.gitb.juandaaguilera.inventariotienda.dao.CategoriaDAO;
import com.gitb.juandaaguilera.inventariotienda.dao.ProductoDAO;
import com.gitb.juandaaguilera.inventariotienda.model.Categoria;
import com.gitb.juandaaguilera.inventariotienda.model.Producto;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana principal. No contiene SQL: solo habla con los DAO.
 */
public class VentanaInventario extends JFrame {

    private static final int STOCK_MINIMO = 5;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtPrecio = new JTextField(20);
    private final JTextField txtCantidad = new JTextField(20);
    private final JComboBox<Categoria> cmbCategoria = new JComboBox<>();
    private final JComboBox<Categoria> cmbFiltro = new JComboBox<>();

    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Precio", "Cant.", "Categoría"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);

    // id del producto cargado en el formulario (-1 = ninguno)
    private int idSeleccionado = -1;

    public VentanaInventario() {
        super("Inventario — Tienda de Don Ernesto");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        construirFormulario();
        construirTabla();

        cargarCategorias();
        agregarEventos();
        refrescarTabla();

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    // ---------- Construcción de la interfaz ----------

    private void construirFormulario() {
        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        agregarFila(campos, gbc, 0, "Nombre:", txtNombre);
        agregarFila(campos, gbc, 1, "Precio:", txtPrecio);
        agregarFila(campos, gbc, 2, "Cantidad:", txtCantidad);
        agregarFila(campos, gbc, 3, "Categoría:", cmbCategoria);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(campos, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila,
            String etiqueta, java.awt.Component campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    private void construirTabla() {
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);

        // Reto: resaltar productos con cantidad menor a 5
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor,
                    boolean seleccionada, boolean foco, int fila, int columna) {
                Component c = super.getTableCellRendererComponent(
                        t, valor, seleccionada, foco, fila, columna);
                if (!seleccionada) {
                    int cantidad = (int) t.getModel().getValueAt(
                            t.convertRowIndexToModel(fila), 3);
                    if (cantidad < STOCK_MINIMO) {
                        c.setBackground(new Color(255, 205, 210));
                        c.setForeground(new Color(150, 0, 0));
                    } else {
                        c.setBackground(t.getBackground());
                        c.setForeground(t.getForeground());
                    }
                }
                return c;
            }
        });

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filtro.add(new JLabel("Filtrar por categoría:"));
        filtro.add(cmbFiltro);
        filtro.add(new JLabel("(en rojo: quedan menos de " + STOCK_MINIMO + " unidades)"));

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        centro.add(filtro, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new java.awt.Dimension(620, 260));
        centro.add(scroll, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }

    // ---------- Datos ----------

    // Llena los combos desde la base de datos con CategoriaDAO
    private void cargarCategorias() {
        try {
            List<Categoria> categorias = categoriaDAO.listar();
            cmbFiltro.addItem(new Categoria(0, "Todas"));
            for (Categoria c : categorias) {
                cmbCategoria.addItem(c);
                cmbFiltro.addItem(c);
            }
        } catch (SQLException e) {
            mostrarError(e.getMessage());
        }
    }

    private void refrescarTabla() {
        try {
            Categoria filtro = (Categoria) cmbFiltro.getSelectedItem();
            List<Producto> productos;
            if (filtro == null || filtro.getIdCategoria() == 0) {
                productos = productoDAO.listar();
            } else {
                productos = productoDAO.listarPorCategoria(filtro.getIdCategoria());
            }

            modeloTabla.setRowCount(0);
            for (Producto p : productos) {
                modeloTabla.addRow(new Object[]{
                    p.getIdProducto(),
                    p.getNombre(),
                    formatoPrecio(p.getPrecio()),
                    p.getCantidad(),
                    p.getCategoria()
                });
            }
        } catch (SQLException e) {
            mostrarError(e.getMessage());
        }
    }

    // ---------- Eventos ----------

    private void agregarEventos() {
        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        cmbFiltro.addActionListener(e -> refrescarTabla());

        // Clic en una fila: cargar el producto en el formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                cargarProductoSeleccionado();
            }
        });
    }

    private void cargarProductoSeleccionado() {
        int fila = tabla.getSelectedRow();
        int id = (int) modeloTabla.getValueAt(fila, 0);
        try {
            Producto p = productoDAO.buscarPorId(id);
            if (p == null) {
                mostrarError("El producto ya no existe en la base de datos.");
                refrescarTabla();
                return;
            }
            idSeleccionado = p.getIdProducto();
            txtNombre.setText(p.getNombre());
            txtPrecio.setText(formatoPrecio(p.getPrecio()));
            txtCantidad.setText(String.valueOf(p.getCantidad()));
            seleccionarCategoria(p.getCategoria().getIdCategoria());
        } catch (SQLException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void guardar() {
        Producto p = leerFormulario();
        if (p == null) {
            return;
        }
        try {
            if (productoDAO.insertar(p)) {
                JOptionPane.showMessageDialog(this, "Producto guardado correctamente.");
                limpiar();
                refrescarTabla();
            } else {
                mostrarError("No se pudo guardar el producto.");
            }
        } catch (SQLException e) {
            mostrarError(e.getMessage());
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) {
            mostrarError("Seleccione un producto de la tabla para actualizarlo.");
            return;
        }
        Producto p = leerFormulario();
        if (p == null) {
            return;
        }
        p.setIdProducto(idSeleccionado);
        try {
            if (productoDAO.actualizar(p)) {
                JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.");
                limpiar();
                refrescarTabla();
            } else {
                mostrarError("No se encontró el producto a actualizar.");
            }
        } catch (SQLException e) {
            mostrarError(e.getMessage());
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            mostrarError("Seleccione un producto de la tabla para eliminarlo.");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea eliminar el producto \"" + txtNombre.getText() + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            if (productoDAO.eliminar(idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.");
                limpiar();
                refrescarTabla();
            } else {
                mostrarError("No se encontró el producto a eliminar.");
            }
        } catch (SQLException e) {
            mostrarError(e.getMessage());
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtCantidad.setText("");
        if (cmbCategoria.getItemCount() > 0) {
            cmbCategoria.setSelectedIndex(0);
        }
        idSeleccionado = -1;
        tabla.clearSelection();
        txtNombre.requestFocus();
    }

    // ---------- Utilidades ----------

    // Valida el formulario y construye el Producto; devuelve null si hay error (ya mostrado)
    private Producto leerFormulario() {
        String nombre = txtNombre.getText().trim();
        String textoPrecio = txtPrecio.getText().trim();
        String textoCantidad = txtCantidad.getText().trim();
        Categoria categoria = (Categoria) cmbCategoria.getSelectedItem();

        if (nombre.isEmpty() || textoPrecio.isEmpty() || textoCantidad.isEmpty()) {
            mostrarError("Todos los campos son obligatorios.");
            return null;
        }
        if (categoria == null) {
            mostrarError("Debe seleccionar una categoría.");
            return null;
        }

        double precio;
        int cantidad;
        try {
            precio = Double.parseDouble(textoPrecio.replace(',', '.'));
        } catch (NumberFormatException e) {
            mostrarError("El precio debe ser un número (ejemplo: 4500 o 2800.50).");
            return null;
        }
        try {
            cantidad = Integer.parseInt(textoCantidad);
        } catch (NumberFormatException e) {
            mostrarError("La cantidad debe ser un número entero.");
            return null;
        }

        try {
            // Las validaciones (precio > 0, cantidad >= 0) están en los setters del modelo
            return new Producto(0, nombre, precio, cantidad, categoria);
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
            return null;
        }
    }

    private void seleccionarCategoria(int idCategoria) {
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            if (cmbCategoria.getItemAt(i).getIdCategoria() == idCategoria) {
                cmbCategoria.setSelectedIndex(i);
                return;
            }
        }
    }

    private String formatoPrecio(double precio) {
        return BigDecimal.valueOf(precio).stripTrailingZeros().toPlainString();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
