package com.example.puestosypintado.controladores;

import com.example.puestosypintado.Database.Conexion;
import com.example.puestosypintado.modelo.Poducto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import static java.lang.Integer.parseInt;

public class ProductoController {

    @FXML private TableView<Poducto> tvInventario;
    @FXML private TableColumn<Poducto, String> colSKU;
    @FXML private TableColumn<Poducto, String> colDescripcion;
    @FXML private TableColumn<Poducto, String> colStock;

    @FXML private TextField txtSKU;
    @FXML private TextField txtDescripcion;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TextField txtStock;
    @FXML private TextField txtStockMinimo;
    @FXML private TextField txtnombre;
    @FXML private TextField txtPrecio;
    @FXML private Button btnBuscarProd;
    @FXML private TextField txtPrecioCom;

    Conexion conexion = new Conexion();

    @FXML
    public void initialize() {
        cmbCategoria.setItems(fillComboBoxCategoria());
        cmbCategoria.setValue(null);
        actualizarLista();
    }

    private String getCategoriaNombre(int idCategoria) {

        String sql = "SELECT nombre FROM Categoria WHERE id = ?";

        try (Connection conn = conexion.estabecerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("nombre");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener categoría: " + e.toString());
        }

        return null;
    }

    private int obtenerIdCategoria(String nombre) {

        String sql = "SELECT id FROM Categoria WHERE nombre = ?";

        try (Connection conn = conexion.estabecerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error obteniendo categoría: " + e);
        }

        return -1;
    }

    private ObservableList<String> fillComboBoxCategoria() {

        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = "SELECT nombre FROM Categoria";

        try (Connection conn = conexion.estabecerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getString("nombre"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error cargando categorías: " + e);
        }

        return lista;
    }

    protected ObservableList<Poducto> observableProducto() {

        ObservableList<Poducto> lista = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Producto";

        try (Connection connection = conexion.estabecerConexion();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Poducto(
                        rs.getString("sku"),
                        rs.getString("descripcion"),
                        rs.getString("stock_actual")
                ));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.toString());
        }

        return lista;
    }

    @FXML
    public void fnBuscarProducto(ActionEvent event) {

        String sku = txtSKU.getText().trim();
        String nombre = txtnombre.getText().trim();

        String sql = null;

        if (!sku.isEmpty()) {
            sql = "SELECT * FROM Producto WHERE sku = " + sku;

        } else if (!nombre.isEmpty()) {
            sql = "SELECT * FROM Producto WHERE nombre = '" + nombre + "'";

        } else {
            JOptionPane.showMessageDialog(null,
                    "Ingresa el ID o nombre del producto para buscar");
            return;
        }

        buscarDatos(sql);
    }

    private void buscarDatos(String sql) {

        try (Connection connection = conexion.estabecerConexion();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                txtSKU.setText(rs.getString("sku"));
                txtnombre.setText(rs.getString("nombre"));
                txtDescripcion.setText(rs.getString("descripcion"));
                txtStock.setText(rs.getString("stock_actual"));
                txtStockMinimo.setText(rs.getString("stock_minimo"));
                txtPrecioCom.setText(rs.getString("precio_compra"));
                txtPrecio.setText(rs.getString("precio_venta"));

                cmbCategoria.setValue(getCategoriaNombre(rs.getInt("categoria_id")));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.toString());
        }
    }

    @FXML
    public void fnGuardarProducto(ActionEvent event) {

        int idCategoria = obtenerIdCategoria(cmbCategoria.getValue());

        double precioCompra = Double.parseDouble(txtPrecioCom.getText().trim());
        double precioVenta = precioCompra * 1.35;

//        if (cmbCategoria.getValue() == null) {
//            JOptionPane.showMessageDialog(null, "Seleccione una categoría");
//            return;
//        }

        String sql = "INSERT INTO Producto " +
                "(nombre, descripcion, categoria_id, stock_actual, stock_minimo, precio_compra, precio_venta) " +
                "VALUES (?,?,?,?,?,?,?)";

        try (Connection connection = conexion.estabecerConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            //ps.setString(1, txtSKU.getText().trim());
            ps.setString(1,txtnombre.getText().trim());
            ps.setString(2, txtDescripcion.getText().trim());
            ps.setInt(3, idCategoria);
            ps.setInt(4, parseInt(txtStock.getText().trim())); // stock_actual
            ps.setInt(5, parseInt(txtStockMinimo.getText().trim()));
            ps.setDouble(6, precioCompra);
            ps.setDouble(7, precioVenta);

            if (ps.executeUpdate() == 1) {
                JOptionPane.showMessageDialog(null, "Producto guardado");
                actualizarLista();
                fnLimpiar(null);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.toString());
        }
    }

    @FXML
    public void fnModificarProducto(ActionEvent event) {

        int idCategoria = obtenerIdCategoria(cmbCategoria.getValue());

        String sql = "UPDATE Producto SET " +
                "nombre=?, descripcion=?, categoria_id=?, stock_actual=?, stock_minimo=?, precio_compra=?, precio_venta=? WHERE sku=?";

        try (Connection connection = conexion.estabecerConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {
        if (parseInt(txtPrecioCom.getText().trim())<=parseInt(txtPrecio.getText().trim())) JOptionPane.showMessageDialog(null,"el precio de venta no puede ser igual o menor que el de compra");
        else {
            ps.setString(1, txtnombre.getText().trim());
            ps.setString(2, txtDescripcion.getText().trim());
            ps.setInt(3, obtenerIdCategoria(cmbCategoria.getValue()));
            ps.setInt(4, parseInt(txtStock.getText().trim()));
            ps.setInt(5, parseInt(txtStockMinimo.getText().trim()));
            ps.setDouble(6, Double.parseDouble(txtPrecioCom.getText().trim()));
            ps.setDouble(7, Double.parseDouble(txtPrecio.getText().trim()));
            ps.setString(8, txtSKU.getText().trim());
        }
            if (ps.executeUpdate() == 1) {
                JOptionPane.showMessageDialog(null, "Producto actualizado");
                actualizarLista();
                fnLimpiar(null);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.toString());
        }
    }

    @FXML
    public void fnBorrarProducto(ActionEvent event) {
        String nombre = txtnombre.getText().trim();
        String sku = txtSKU.getText().trim();

        if (sku.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese o busque un producto primero.");
            return;
        }

        Alert alerta = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Seguro que desea eliminar el producto: " + nombre + "?",
                ButtonType.YES,
                ButtonType.NO
        );

        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.YES) {

            String sql = "DELETE FROM Producto WHERE sku='" + sku + "'";

            try (Connection connection = conexion.estabecerConexion();
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                if (ps.executeUpdate() == 1) {
                    JOptionPane.showMessageDialog(null, "Producto eliminado correctamente");
                    actualizarLista();
                    fnLimpiar(null);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.toString());
            }
        }
    }

    public void actualizarLista() {

        colSKU.setCellValueFactory(cell -> cell.getValue().skuProperty());
        colDescripcion.setCellValueFactory(cell -> cell.getValue().nombreProperty());
        colStock.setCellValueFactory(cell -> cell.getValue().stockProperty());

        tvInventario.setItems(observableProducto());
    }

    @FXML
    public void fnLimpiar(ActionEvent event) {
        txtSKU.clear();
        txtDescripcion.clear();
        txtnombre.clear();
        cmbCategoria.setValue(null);
        txtStock.clear();
        txtStockMinimo.clear();
        txtPrecioCom.clear();
        txtPrecio.clear();
    }
}