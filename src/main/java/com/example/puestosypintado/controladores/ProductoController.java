package com.example.puestosypintado.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javax.swing.JOptionPane;

public class ProductoController {

    // --- TABLA Y COLUMNAS ---
    // Nota: Reemplaza "Object" por tu clase modelo, ej: "Producto"
    @FXML private TableView<Object> tvInventario;
    @FXML private TableColumn<Object, String> colSKU;
    @FXML private TableColumn<Object, String> colDescripcion;
    @FXML private TableColumn<Object, Integer> colStock;

    // --- CAMPOS DE TEXTO Y COMBOBOX ---
    @FXML private TextField txtSKU;
    @FXML private TextField txtDescripcion;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TextField txtProveedor;
    @FXML private TextField txtUbicacion;
    @FXML private TextField txtStock;
    @FXML private TextField txtStockMinimo;
    @FXML private TextField txtPrecio;
    @FXML private Button btnBuscarProd;

    @FXML
    public void initialize() {
        // Inicializar el ComboBox con categorías de ejemplo
        cmbCategoria.getItems().addAll("Pintura", "Repuestos", "Consumibles", "Herramientas");
    }

    @FXML
    public void fnBuscarProducto(ActionEvent event) {
        String idBusqueda = txtSKU.getText();
        JOptionPane.showMessageDialog(null, "Buscando producto con ID: " + idBusqueda);
        // Aquí va tu lógica SELECT * FROM Inventario WHERE id = ...
    }

    @FXML
    public void fnGuardarProducto(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Guardando nuevo producto en el inventario...");
        // Lógica de INSERT INTO
    }

    @FXML
    public void fnModificarProducto(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Actualizando producto...");
        // Lógica de UPDATE
    }

    @FXML
    public void fnBorrarProducto(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Producto eliminado del sistema.");
        // Lógica de DELETE
    }

    @FXML
    public void fnLimpiar(ActionEvent event) {
        txtSKU.clear();
        txtDescripcion.clear();
        cmbCategoria.setValue(null);
        txtProveedor.clear();
        txtUbicacion.clear();
        txtStock.clear();
        txtStockMinimo.clear();
        txtPrecio.clear();
    }
}