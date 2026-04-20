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
    @FXML private ComboBox<String> cmbProvincia;
    @FXML private ComboBox<String> cmbSector;
    @FXML private TableView<Cliente> tvclientes;
    @FXML private TableColumn<Cliente, String> colcedula;
    @FXML private TableColumn<Cliente, String> colnombre;

    public void initialize() {
        cmbestado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));

        cmbProvincia.setItems(FXCollections.observableArrayList("Santo Domingo", "Santiago de los Caballeros", "La Vega", "Puerto Plata", "San Cristóbal", "La Romana"));

        //cmbSector.setItems(FXCollections.observableArrayList("Los Mina", "Villa Mella", "Gascue", "Naco", "Bella Vista", "Alma Rosa", "Ensanche Ozama"));

        cmbProvincia.setOnAction(e -> {
            String provincia = cmbProvincia.getValue();
            switch (provincia) {
                case "Santo Domingo":
                    cmbSector.setItems(FXCollections.observableArrayList(
                            "Los Mina", "Villa Mella", "Naco", "Gascue", "Bella Vista"
                    ));
                    break;

                case "Santiago de los Caballeros":
                    cmbSector.setItems(FXCollections.observableArrayList(
                            "Los Jardines", "Gurabo", "Cienfuegos", "Pekín", "Bella Vista"
                    ));
                    break;

                case "La Vega":
                    cmbSector.setItems(FXCollections.observableArrayList(
                            "Centro", "Villa Rosa", "Cutupú", "Burende", "Los Pomos"
                    ));
                    break;

                case "Puerto Plata":
                    cmbSector.setItems(FXCollections.observableArrayList(
                            "Centro", "Ensanche Dubocq", "Cerro Alto", "Padre Granero", "Cofresí"
                    ));
                    break;

                case "San Cristóbal":
                    cmbSector.setItems(FXCollections.observableArrayList(
                            "Madre Vieja", "Canastica", "Haina", "Yaguate", "Najayo"
                    ));
                    break;

                case "La Romana":
                    cmbSector.setItems(FXCollections.observableArrayList(
                            "Villa Verde", "Caleta", "Savica", "Quisqueya", "Villa Hermosa"
                    ));
                    break;
            }});


        cmbestado.setValue(null);
        cmbProvincia.setValue(null);
        cmbSector.setValue(null);

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
        String Provincia = cmbProvincia.getValue();
        String sector = cmbSector.getValue();

        if (nombre.isEmpty() || cedula.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nombre, Cédula y Teléfono son obligatorios.");
            return;
        } else if (existeCedula(cedula)) {
            JOptionPane.showMessageDialog(null, "LA CÉDULA YA ESTA GUARDADA EN LA BD!!");
        }

        String sql = "INSERT INTO Clientes (nombre, cedula, telefono, email, direccion, provincia, sector, estado) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection connection = conexion.estabecerConexion();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, cedula);
            pstmt.setString(3, telefono);
            pstmt.setString(4, email);
            pstmt.setString(5, direccion);
            pstmt.setString(6, Provincia);
            pstmt.setString(7, sector);
            pstmt.setString(8, estado);

            if (pstmt.executeUpdate() == 1) {
                JOptionPane.showMessageDialog(null, "Cliente guardado exitosamente");
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
        String sql = "UPDATE Clientes SET nombre='" + txtnombre.getText().trim() +
                "', cedula='" + txtcedula.getText().trim() +
                "', telefono='" + txttelefono.getText().trim() +
                "', email='" + txtemail.getText().trim() +
                "', direccion='" + txtdireccion.getText().trim() +
                "', provincia='" + cmbProvincia.getValue() +
                "', sector='" + cmbSector.getValue() +
                "', estado='" + cmbestado.getValue() + "' WHERE id_cliente=" + id;
        ejecutarSQL(sql);
    }

    @FXML
    public void fnBorrarCliente(ActionEvent event) {
        int id = Integer.parseInt(txtid_Cliente.getText().trim());
        String nombre = txtnombre.getText().trim();
        if(nombre.isEmpty()) System.out.println("No se puede borrar a la nada, rey");

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que desea borrar a " + nombre + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.YES) {
            String sql = "DELETE FROM Clientes WHERE id_cliente='" + id + "'";
            ejecutarSQL(sql);
        }
    }

    @FXML
    public void fnBuscarCliente(ActionEvent event) {
        if (!txtid_Cliente.getText().trim().isEmpty()){
            String id = txtid_Cliente.getText().trim();
            String sql = "SELECT * FROM Clientes WHERE id_cliente=" + id;
            buscarDatos(sql);
        } else if (!txtcedula.getText().trim().isEmpty()) {
            String cedula = txtcedula.getText().trim();
            String sql = "SELECT * FROM Clientes WHERE cedula='" + cedula + "'";
            buscarDatos(sql);
        } else if(!txtnombre.getText().trim().isEmpty()) {
            String nombre = txtnombre.getText().trim();
            String sql = "SELECT * FROM Clientes WHERE nombre='" + nombre + "'";
            buscarDatos(sql);
        } else {
            JOptionPane.showMessageDialog(null,"Y se supone que busco el valor de x o te leo la mente? Pon el ID, cédula o nombre");
        }
    }

    private void buscarDatos(String sql) {
        try (Connection connection = conexion.estabecerConexion();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery();
        ) {
            if (resultSet.next()) {
                this.txtid_Cliente.setText(resultSet.getString("id_cliente"));
                this.txtnombre.setText(resultSet.getString("nombre"));
                this.txtcedula.setText(resultSet.getString("cedula"));
                this.txttelefono.setText(resultSet.getString("telefono"));
                this.txtemail.setText(resultSet.getString("email"));
                this.txtdireccion.setText(resultSet.getString("direccion"));
                String provincia = resultSet.getString("provincia");
                this.cmbProvincia.getSelectionModel().select(provincia.trim());
                String sector = resultSet.getString("sector");
                this.cmbSector.getSelectionModel().select(sector.trim());
                String estado = resultSet.getString("estado");
                this.cmbestado.getSelectionModel().select(estado.trim());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog
                    (null,"Error:" + e.toString());
        }
        //return provincia;
    }


    public void ejecutarSQL(String sql) {
        try (Connection connection = conexion.estabecerConexion();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            int result = preparedStatement.executeUpdate();
            if (result == 1 && ValidarCliente()){
                JOptionPane.showMessageDialog(null,"Acción ejecutada corrrectamente");
                actualizarLista();
                fnLimpiar(null);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog
                    (null,"Error:" + e.toString());
        }
    }

    public void actualizarLista() {
        colcedula.setCellValueFactory(cellData -> cellData.getValue().cedulaProperty());
        colnombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());

        tvclientes.setItems(observableCliente());
    }

    private boolean ValidarCliente() {

        return !txtnombre.getText().trim().isEmpty() &&
                !txtcedula.getText().trim().isEmpty() &&
                !txttelefono.getText().trim().isEmpty() &&
                !txtemail.getText().trim().isEmpty() &&
                !txtdireccion.getText().trim().isEmpty() &&
                cmbProvincia.getValue() != null &&
                cmbSector.getValue() != null &&
                cmbestado.getValue() != null;
    }

    private boolean existeCedula(String cedula) {
        String sql = "SELECT COUNT(*) FROM Clientes WHERE id_cliente = ?";
        try (Connection connection = conexion.estabecerConexion();
             PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, cedula);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // true si existe
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar: " + e.toString());
        }
        return false;
    }

    @FXML
    public void fnLimpiar(ActionEvent event) {
        txtid_Cliente.clear();
        txtnombre.clear();
        txtcedula.clear();
        txttelefono.clear();
        txtemail.clear();
        txtdireccion.clear();
        cmbSector.setValue(null);
        cmbProvincia.setValue(null);
        cmbestado.setValue(null);
    }
}