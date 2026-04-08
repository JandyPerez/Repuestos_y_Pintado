package com.example.puestosypintado.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javax.swing.JOptionPane;

public class FacturacionController {

    // --- TABLA Y COLUMNAS ---
    // Nota: Reemplaza "Object" por tu clase modelo, ej: "Factura"
    @FXML private TableView<Object> tvFacturas;
    @FXML private TableColumn<Object, String> colNumFac;
    @FXML private TableColumn<Object, String> colClienteFac;
    @FXML private TableColumn<Object, String> colEstadoFac;

    // --- CAMPOS DE TEXTO Y COMBOBOX ---
    @FXML private TextField txtBuscarFac;
    @FXML private TextField txtClienteInfo;
    @FXML private TextField txtTotalFac;
    @FXML private TextField txtBalance;
    @FXML private TextField txtAbono;
    @FXML private ComboBox<String> cmbMetodoPago;

    @FXML
    public void initialize() {
        // Inicializar métodos de pago
        cmbMetodoPago.getItems().addAll("Efectivo", "Tarjeta de Crédito", "Transferencia", "Cheque");
    }

    @FXML
    public void fnBuscarFactura(ActionEvent event) {
        String numFactura = txtBuscarFac.getText();
        JOptionPane.showMessageDialog(null, "Buscando datos de la factura #" + numFactura);
        // Aquí cargas los datos del cliente, el total y el balance en los TextField deshabilitados
    }

    @FXML
    public void fnAplicarPago(ActionEvent event) {
        if(cmbMetodoPago.getValue() == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un método de pago.");
            return;
        }
        JOptionPane.showMessageDialog(null, "Pago de $" + txtAbono.getText() + " aplicado correctamente.");
        // Lógica para restar el abono del balance en la base de datos
    }

    @FXML
    public void fnReemitirFac(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Generando PDF de la factura nuevamente...");
    }

    @FXML
    public void fnAnularFactura(ActionEvent event) {
        int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de anular esta factura?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION){
            JOptionPane.showMessageDialog(null, "Factura anulada.");
            // UPDATE Facturas SET estado = 'Anulada'
        }
    }

    @FXML
    public void fnLimpiarPagos(ActionEvent event) {
        txtBuscarFac.clear();
        txtClienteInfo.clear();
        txtTotalFac.clear();
        txtBalance.clear();
        txtAbono.clear();
        cmbMetodoPago.setValue(null);
    }
}