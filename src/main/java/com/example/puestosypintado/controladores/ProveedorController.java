package com.example.puestosypintado.controladores;

// import com.example.taller.Database.Conexion;
import com.example.puestosypintado.modelo.Proveedor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javax.swing.JOptionPane;

public class ProveedorController {

    // Conexion conexion = new Conexion();

    // --- ELEMENTOS FXML PROVEEDOR ---
    @FXML private TableView<Proveedor> tvProveedores;
    @FXML private TableColumn<Proveedor, String> colIdProv, colEmpresaProv, colContactoProv, colTelProv, colCategoriaProv;

    @FXML private TextField txtIdProv, txtRncProv, txtEmpresaProv, txtDireccionProv, txtTelProv, txtEmailProv, txtContactoProv, txtTelContactoProv;
    @FXML private TextArea taNotasProv;
    @FXML private ComboBox<String> cmbCategoriaProv, cmbEstadoProv;

    private ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cmbCategoriaProv.setItems(FXCollections.observableArrayList("Pintura", "Repuestos", "Consumibles", "Herramientas", "Servicios Externos"));
        cmbEstadoProv.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbEstadoProv.getSelectionModel().selectFirst();

        colIdProv.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colEmpresaProv.setCellValueFactory(cellData -> cellData.getValue().empresaProperty());
        colContactoProv.setCellValueFactory(cellData -> cellData.getValue().contactoProperty());
        colTelProv.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());
        colCategoriaProv.setCellValueFactory(cellData -> cellData.getValue().categoriaProperty());

        tvProveedores.setItems(listaProveedores);
    }

    @FXML
    public void fnGuardarProveedor(ActionEvent event) {
        if (!validarFormProv()) return;

        if (existeRnc(txtRncProv.getText().trim())) {
            JOptionPane.showMessageDialog(null, "¡ALERTA! Un proveedor con este RNC/NIT ya está registrado.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Proveedor guardado correctamente.");
        fnLimpiarProv(null);
    }

    @FXML
    public void fnModificarProveedor(ActionEvent event) {
        if (txtIdProv.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, busque un proveedor primero.");
            return;
        }
        JOptionPane.showMessageDialog(null, "Datos del proveedor actualizados.");
        fnLimpiarProv(null);
    }

    @FXML
    public void fnBorrarProveedor(ActionEvent event) {
        String empresa = txtEmpresaProv.getText().trim();
        if(empresa.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(null, "¿Seguro que desea borrar al proveedor " + empresa + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(null, "Proveedor eliminado.");
            fnLimpiarProv(null);
        }
    }

    @FXML
    public void fnBuscarProveedor(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Buscando proveedor...");
    }

    @FXML
    public void fnLimpiarProv(ActionEvent event) {
        txtIdProv.clear(); txtRncProv.clear(); txtEmpresaProv.clear(); txtDireccionProv.clear();
        txtTelProv.clear(); txtEmailProv.clear(); txtContactoProv.clear(); txtTelContactoProv.clear();
        taNotasProv.clear(); cmbCategoriaProv.setValue(null);
        cmbEstadoProv.getSelectionModel().selectFirst();
    }

    private boolean existeRnc(String rnc) {
        return false; // Simulación: SELECT * FROM Proveedor WHERE rnc_nit = ?
    }

    private boolean validarFormProv() {
        if (txtRncProv.getText().isEmpty() || txtEmpresaProv.getText().isEmpty() ||
                txtTelProv.getText().isEmpty() || cmbCategoriaProv.getValue() == null) {
            JOptionPane.showMessageDialog(null, "Llene los campos obligatorios (*).");
            return false;
        }
        return true;
    }
}