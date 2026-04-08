package com.example.puestosypintado.controladores;

import com.example.puestosypintado.Database.Conexion;
import com.example.puestosypintado.modelo.Vehiculo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.Optional;

public class VehiculoController {

    Conexion conexion = new Conexion();

    @FXML private TextField txtIdVehiculo, txtMarca, txtModelo, txtAno, txtColor, txtPlaca, txtVIN;
    @FXML private TextArea txtNotas;
    @FXML private ComboBox<String> cmbCliente;

    @FXML private TableView<Vehiculo> tvVehiculos;
    @FXML private TableColumn<Vehiculo, String> colPlaca;
    @FXML private TableColumn<Vehiculo, String> colMarcaModelo;

    public void initialize() {
        colPlaca.setCellValueFactory(cellData -> cellData.getValue().placaProperty());
        colMarcaModelo.setCellValueFactory(cellData -> cellData.getValue().marcaModeloProperty());
        actualizarLista();
        cargarClientes();
    }

    private void cargarClientes() {
        // Aquí idealmente cargas los clientes desde la BD. Por ahora, un dato de ejemplo:
        cmbCliente.setItems(FXCollections.observableArrayList("001-0000000-1 - Juan Perez"));
    }

    @FXML
    protected ObservableList<Vehiculo> observableVehiculo() {
        ObservableList<Vehiculo> lista = FXCollections.observableArrayList();
        String sql = "SELECT Placa, CONCAT(Marca, ' ', Modelo) AS MarcaModelo FROM Vehiculo";

        try (Connection conn = conexion.estabecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Vehiculo(rs.getString("Placa"), rs.getString("MarcaModelo")));
            }
        } catch (Exception e) {
            System.out.println("Error al cargar tabla vehículos: " + e.toString());
        }
        return lista;
    }

    @FXML
    protected void fnGuardarVehiculo(ActionEvent event) {
        String placa = txtPlaca.getText().trim();
        String vin = txtVIN.getText().trim();

        if (placa.isEmpty() || vin.isEmpty() || cmbCliente.getValue() == null) {
            JOptionPane.showMessageDialog(null, "Cliente, Placa y VIN son obligatorios.");
            return;
        }

        // Regla de Negocio: Validar Duplicados
        if (existeVehiculo(placa, vin)) {
            JOptionPane.showMessageDialog(null, "Alerta: Ya existe un vehículo registrado con esta Placa o VIN. Inicie proceso de conciliación.");
            return; // Bloquea el guardado
        }

        String sql = "INSERT INTO Vehiculo (Marca, Modelo, Ano, Color, Placa, VIN, Notas) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = conexion.estabecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, txtMarca.getText().trim());
            pstmt.setString(2, txtModelo.getText().trim());
            pstmt.setString(3, txtAno.getText().trim());
            pstmt.setString(4, txtColor.getText().trim());
            pstmt.setString(5, placa);
            pstmt.setString(6, vin);
            pstmt.setString(7, txtNotas.getText().trim());

            if (pstmt.executeUpdate() == 1) {
                JOptionPane.showMessageDialog(null, "Vehículo registrado correctamente.");
                actualizarLista();
                fnLimpiar(null);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar: " + e.getMessage());
        }
    }

    private boolean existeVehiculo(String placa, String vin) {
        String sql = "SELECT id_vehiculo FROM Vehiculo WHERE Placa = ? OR VIN = ?";
        try (Connection conn = conexion.estabecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, placa);
            pstmt.setString(2, vin);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Retorna true si encontró un registro
        } catch (SQLException e) {
            return false;
        }
    }

    @FXML
    public void fnModificarVehiculo(ActionEvent event) {
        // Lógica de UPDATE similar al ClienteController
        JOptionPane.showMessageDialog(null, "Función Modificar en construcción...");
    }

    @FXML
    public void fnBorrarVehiculo(ActionEvent event) {
        String placa = txtPlaca.getText().trim();
        if(placa.isEmpty()) return;

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, "¿Borrar el vehículo con placa " + placa + "?", ButtonType.YES, ButtonType.NO);
        if (alerta.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try (Connection conn = conexion.estabecerConexion();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Vehiculo WHERE Placa = ?")) {
                pstmt.setString(1, placa);
                pstmt.executeUpdate();
                actualizarLista();
                fnLimpiar(null);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al borrar: " + e.getMessage());
            }
        }
    }

    @FXML
    public void fnBuscarVehiculo(ActionEvent event) {
        // Lógica de SELECT WHERE id = ?
    }

    public void actualizarLista() { tvVehiculos.setItems(observableVehiculo()); }

    @FXML
    public void fnLimpiar(ActionEvent event) {
        txtIdVehiculo.clear(); txtMarca.clear(); txtModelo.clear();
        txtAno.clear(); txtColor.clear(); txtPlaca.clear();
        txtVIN.clear(); txtNotas.clear(); cmbCliente.setValue(null);
    }
}