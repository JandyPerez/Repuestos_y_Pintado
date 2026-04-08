package com.example.puestosypintado.controladores;

import com.example.puestosypintado.Database.*;
import com.example.puestosypintado.modelo.Cliente; // Asegúrate de crear esta clase Modelo
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.Optional;

public class ClientesControlador {

    Conexion conexion = new Conexion();

    @FXML private TextField txtid_Cliente;
    @FXML private TextField txtnombre;
    @FXML private TextField txtcedula;
    @FXML private TextField txttelefono;
    @FXML private TextField txtemail;
    @FXML private TextField txtdireccion;
    @FXML private ComboBox<String> cmbestado;

    @FXML private TableView<Cliente> tvclientes;
    @FXML private TableColumn<Cliente, String> colcedula;
    @FXML private TableColumn<Cliente, String> colnombre;

    public void initialize() {
        cmbestado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbestado.getSelectionModel().selectFirst();

        // Vincular columnas (Asegúrate de tener las Properties en tu clase Cliente)
        colcedula.setCellValueFactory(cellData -> cellData.getValue().cedulaProperty());
        colnombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());

        tvclientes.setItems(observableCliente());
    }

    @FXML
    protected ObservableList<Cliente> observableCliente() {
        ObservableList<Cliente> lista = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Clientes";

        try (Connection connection = conexion.estabecerConexion();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Cliente(
                        rs.getString("Cedula"),
                        rs.getString("Nombre")
                ));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener clientes: " + e.toString());
        }
        return lista;
    }

    @FXML
    protected void fnGuardarCliente(ActionEvent event) {
        String nombre = txtnombre.getText().trim();
        String cedula = txtcedula.getText().trim();
        String telefono = txttelefono.getText().trim();
        String email = txtemail.getText().trim();
        String direccion = txtdireccion.getText().trim();
        String estado = cmbestado.getValue();

        if (nombre.isEmpty() || cedula.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nombre, Cédula y Teléfono son obligatorios.");
            return;
        }

        String sql = "INSERT INTO Clientes (Nombre, Cedula, Telefono, Email, Direccion, Estado) VALUES (?,?,?,?,?,?)";
        try (Connection connection = conexion.estabecerConexion();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, cedula);
            pstmt.setString(3, telefono);
            pstmt.setString(4, email);
            pstmt.setString(5, direccion);
            pstmt.setString(6, estado);

            if (pstmt.executeUpdate() == 1) {
                JOptionPane.showMessageDialog(null, "Cliente guardado exitosamente :DD");
                actualizarLista();
                fnLimpiar(null);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar: " + e.toString());
        }
    }

    @FXML
    public void fnModificarCliente(ActionEvent event) {
        if (txtid_Cliente.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Busque un cliente primero para editarlo.");
            return;
        }

        int id = Integer.parseInt(txtid_Cliente.getText().trim());
        String sql = "UPDATE Clientes SET Nombre='" + txtnombre.getText().trim() +
                "', Cedula='" + txtcedula.getText().trim() +
                "', Telefono='" + txttelefono.getText().trim() +
                "', Email='" + txtemail.getText().trim() +
                "', Direccion='" + txtdireccion.getText().trim() +
                "', Estado='" + cmbestado.getValue() + "' WHERE id=" + id;
        ejecutarSQL(sql);
    }

    @FXML
    public void fnBorrarCliente(ActionEvent event) {
        String nombre = txtnombre.getText().trim();
        if(nombre.isEmpty()) return;

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que desea borrar a " + nombre + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.YES) {
            String sql = "DELETE FROM Clientes WHERE Cedula='" + txtcedula.getText().trim() + "'";
            ejecutarSQL(sql);
        }
    }

    @FXML
    public void fnBuscarCliente(ActionEvent event) {
        String nombre = txtnombre.getText().trim();
        String sql = "SELECT * FROM Clientes WHERE Nombre='" + nombre + "'";

        try (Connection connection = conexion.estabecerConexion();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                txtid_Cliente.setText(rs.getString(" id"));
                txtnombre.setText(rs.getString("Nombre"));
                txttelefono.setText(rs.getString("Telefono"));
                txtemail.setText(rs.getString("Email"));
                txtdireccion.setText(rs.getString("Direccion"));
                cmbestado.setValue(rs.getString("Estado"));
            } else {
                JOptionPane.showMessageDialog(null, "Cliente no encontrado.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.toString());
        }
    }

    public void ejecutarSQL(String sql) {
        try (Connection connection = conexion.estabecerConexion();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Acción ejecutada correctamente");
                actualizarLista();
                fnLimpiar(null);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error BD: " + e.toString());
        }
    }

    public void actualizarLista() {
        tvclientes.setItems(observableCliente());
    }

    @FXML
    public void fnLimpiar(ActionEvent event) {
        txtid_Cliente.clear();
        txtnombre.clear();
        txtcedula.clear();
        txttelefono.clear();
        txtemail.clear();
        txtdireccion.clear();
        cmbestado.getSelectionModel().selectFirst();
    }
}