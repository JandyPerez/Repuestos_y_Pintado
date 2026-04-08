package com.example.puestosypintado.controladores;

// import com.example.taller.Database.Conexion;
import com.example.puestosypintado.Database.Conexion;
import com.example.puestosypintado.modelo.Empleado;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javax.swing.JOptionPane;

public class EmpleadoController {

    Conexion conexion = new Conexion();

    // --- ELEMENTOS FXML EMPLEADO ---
    @FXML private TableView<Empleado> tvEmpleados;
    @FXML private TableColumn<Empleado, String> colIdEmp, colNombreEmp, colPuestoEmp, colTelEmp;
    @FXML private TextField txtIdEmp, txtNombreEmp, txtCedulaEmp, txtDireccionEmp, txtTelEmp, txtEmailEmp, txtSalarioEmp;
    @FXML private ComboBox<String> cmbPuestoEmp, cmbEstadoEmp;
    @FXML private DatePicker dpFechaContratEmp;

    private ObservableList<Empleado> listaEmpleados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cmbPuestoEmp.setItems(FXCollections.observableArrayList("Mecánico General", "Pintor Automotriz", "Chapista", "Detallista", "Recepcionista", "Gerente"));
        cmbEstadoEmp.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbEstadoEmp.getSelectionModel().selectFirst();

        colIdEmp.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colNombreEmp.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colPuestoEmp.setCellValueFactory(cellData -> cellData.getValue().puestoProperty());
        colTelEmp.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());

        tvEmpleados.setItems(listaEmpleados);
    }

    @FXML
    public void fnGuardarEmpleado(ActionEvent event) {
        if (!validarFormEmp()) return;

        if (existeCedula(txtCedulaEmp.getText().trim())) {
            JOptionPane.showMessageDialog(null, "¡ALERTA! Un empleado con esta Cédula/RNC ya está registrado.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Empleado guardado correctamente.");
        fnLimpiarEmp(null);
    }

    @FXML
    public void fnModificarEmpleado(ActionEvent event) {
        if (txtIdEmp.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, busque un empleado primero.");
            return;
        }
        JOptionPane.showMessageDialog(null, "Datos del empleado actualizados.");
        fnLimpiarEmp(null);
    }

    @FXML
    public void fnBorrarEmpleado(ActionEvent event) {
        String nombre = txtNombreEmp.getText().trim();
        if(nombre.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(null, "¿Seguro que desea borrar a " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(null, "Empleado eliminado.");
            fnLimpiarEmp(null);
        }
    }

    @FXML
    public void fnBuscarEmpleado(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Buscando empleado...");
    }

    @FXML
    public void fnLimpiarEmp(ActionEvent event) {
        txtIdEmp.clear(); txtNombreEmp.clear(); txtCedulaEmp.clear(); txtDireccionEmp.clear();
        txtTelEmp.clear(); txtEmailEmp.clear(); txtSalarioEmp.clear();
        cmbPuestoEmp.setValue(null); dpFechaContratEmp.setValue(null);
        cmbEstadoEmp.getSelectionModel().selectFirst();
    }

    private boolean existeCedula(String cedula) {
        return false; // Simulación: SELECT * FROM Empleado WHERE cedula_rnc = ?
    }

    private boolean validarFormEmp() {
        if (txtNombreEmp.getText().isEmpty() || txtCedulaEmp.getText().isEmpty() ||
                txtTelEmp.getText().isEmpty() || cmbPuestoEmp.getValue() == null) {
            JOptionPane.showMessageDialog(null, "Llene los campos obligatorios (*).");
            return false;
        }
        return true;
    }
}