package com.example.puestosypintado.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javax.swing.JOptionPane;

public class VentasController {

    // --- TABLA Y COLUMNAS ---
    // Nota: Reemplaza "Object" por tu clase modelo, ej: "ItemCarrito"
    @FXML private TableView<Object> tvCarrito;
    @FXML private TableColumn<Object, String> colProducto;
    @FXML private TableColumn<Object, Integer> colCant;
    @FXML private TableColumn<Object, Double> colSubtotal;

    // --- ELEMENTOS DE LA INTERFAZ ---
    @FXML private Label lblImpuestos;
    @FXML private Label lblTotalCarrito;
    @FXML private ComboBox<String> cmbClienteVenta;
    @FXML private TextField txtBuscadorProd;
    @FXML private Label lblStock;
    @FXML private TextField txtCantidad;

    @FXML
    public void initialize() {
        // Inicializar lista de clientes rápida
        cmbClienteVenta.getItems().addAll("Consumidor Final", "Cliente Frecuente 1", "Taller Asociado");
    }

    @FXML
    public void fnVerificarStock(ActionEvent event) {
        String producto = txtBuscadorProd.getText();
        JOptionPane.showMessageDialog(null, "Consultando stock de: " + producto);
        // Lógica para buscar el producto en BD y poner el número en lblStock
        lblStock.setText("15"); // Ejemplo estático
    }

    @FXML
    public void fnAgregarAlCarrito(ActionEvent event) {
        String cantidad = txtCantidad.getText();
        JOptionPane.showMessageDialog(null, "Agregando " + cantidad + " unidad(es) al carrito.");
        // Lógica para añadir objeto a la tabla, calcular subtotal, impuestos e ITBIS
    }

    @FXML
    public void fnProcesarPago(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Procesando venta y enviando a imprimir...");
        // Lógica de INSERT a la tabla de Ventas y detalle de ventas, restar stock del Inventario
        fnCancelarVenta(null); // Limpiamos después de cobrar
    }

    @FXML
    public void fnPedidoEspecial(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Abriendo ventana de Pedidos Especiales (Sin stock actual)");
    }

    @FXML
    public void fnCancelarVenta(ActionEvent event) {
        txtBuscadorProd.clear();
        txtCantidad.clear();
        lblStock.setText("0");
        lblImpuestos.setText("$ 0.00");
        lblTotalCarrito.setText("$ 0.00");
        cmbClienteVenta.setValue(null);
        tvCarrito.getItems().clear(); // Limpia la tabla del carrito
    }
}