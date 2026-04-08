package com.example.puestosypintado.controladores;

import com.example.puestosypintado.Database.Conexion;
import com.example.puestosypintado.modelo.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javax.swing.*;
import java.sql.*;
import java.util.Optional;

import static java.lang.Integer.parseInt;

public class UsuarioController {

    Conexion conexion = new Conexion();

    // --- ELEMENTOS FXML DE LA TABLA ---
    @FXML private TableView<Usuario> tvUsuarios;
    @FXML private TableColumn<Usuario, String> colIdUsuario;
    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colEstado;

    // --- ELEMENTOS FXML DEL FORMULARIO ---
    @FXML private TextField txtIdUsuario;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena; // Usamos PasswordField para la contraseña
    @FXML private ComboBox<String> cmbRol;
    @FXML private ComboBox<String> cmbEstado;

    public void initialize() {
        // Llenar los ComboBox con opciones por defecto
        cmbRol.setItems(FXCollections.observableArrayList("Administrador", "Mecánico", "Recepcionista", "Cajero"));
        cmbEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbEstado.getSelectionModel().selectFirst();

        actualizarLista();
    }

    // =======================================================
    // 1. GUARDAR (INSERT)
    // =======================================================
    public void Guardar(String nombre, String apellido, String usuario, String contrasena, String rol, String estado) {
        String sql = "Insert into Usuario(nombre, apellido, nombre_usuario, contrasena, rol, estado) values(?,?,?,?,?,?)";
        int result;
        try {
            Connection connection = conexion.estabecerConexion();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);
            pstmt.setString(3, usuario);
            pstmt.setString(4, contrasena);
            pstmt.setString(5, rol);
            pstmt.setString(6, estado);

            result = pstmt.executeUpdate();
            if (result == 1) {
                JOptionPane.showMessageDialog(null, "El usuario se ha registrado correctamente");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar los datos: " + e.toString());
        }
    }

    @FXML
    protected void fnGuardarUsuario(ActionEvent actionEvent) {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        String rol = "";
        if(cmbRol.getValue() != null) rol = cmbRol.getValue();

        String estado = "";
        if(cmbEstado.getValue() != null) estado = cmbEstado.getValue();

        if (nombre.isEmpty() || apellido.isEmpty() || usuario.isEmpty() || contrasena.isEmpty() || rol.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Llene todos los espacios en blanco.");
        } else {
            Guardar(nombre, apellido, usuario, contrasena, rol, estado);
            actualizarLista();
            limpiar();
        }
    }

    // =======================================================
    // 2. MOSTRAR DATOS EN TABLA (SELECT ALL)
    // =======================================================
    @FXML
    protected ObservableList<Usuario> observableUsuario() {
        ObservableList<Usuario> lista = FXCollections.observableArrayList();
        String sql = "select * from Usuario";

        try (Connection connection = conexion.estabecerConexion();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery();
        ) {
            while (resultSet.next()) {
                lista.add(new Usuario(
                        resultSet.getString("id_usuario"),
                        resultSet.getString("nombre_usuario"),
                        resultSet.getString("rol"),
                        resultSet.getString("estado")
                ));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener datos: " + e.toString());
        }
        return lista;
    }

    public void actualizarLista() {
        colIdUsuario.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colUsuario.setCellValueFactory(cellData -> cellData.getValue().usuarioProperty());
        colRol.setCellValueFactory(cellData -> cellData.getValue().rolProperty());
        colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());

        tvUsuarios.setItems(observableUsuario());
    }

    // =======================================================
    // 3. BUSCAR (SELECT ONE)
    // =======================================================
    public void fnBuscarUsuario(ActionEvent actionEvent) {
        String username = this.txtUsuario.getText().trim();
        if(username.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Escriba un 'Usuario' (Username) para buscar.");
            return;
        }
        // En este caso, buscaremos por el nombre de usuario (username)
        String sql = "Select * from Usuario where nombre_usuario='" + username + "';";
        buscarDatos(sql);
    }

    private void buscarDatos(String sql) {
        try (Connection connection = conexion.estabecerConexion();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery();
        ) {
            if (resultSet.next()) {
                this.txtIdUsuario.setText(resultSet.getString("id_usuario"));
                this.txtNombre.setText(resultSet.getString("nombre"));
                this.txtApellido.setText(resultSet.getString("apellido"));
                this.txtUsuario.setText(resultSet.getString("nombre_usuario"));
                this.txtContrasena.setText(resultSet.getString("contrasena"));

                String rol = resultSet.getString("rol");
                this.cmbRol.getSelectionModel().select(rol);

                String estado = resultSet.getString("estado");
                this.cmbEstado.getSelectionModel().select(estado);
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró ningún registro con ese nombre de usuario.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.toString());
        }
    }

    // =======================================================
    // 4. MODIFICAR (UPDATE)
    // =======================================================
    public void fnModificarUsuario(ActionEvent actionEvent) {
        if(txtIdUsuario.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Busque un usuario primero para modificarlo.");
            return;
        }

        int id = parseInt(this.txtIdUsuario.getText().trim());
        String nombre = this.txtNombre.getText().trim();
        String apellido = this.txtApellido.getText().trim();
        String usuario = this.txtUsuario.getText().trim();
        String contrasena = this.txtContrasena.getText().trim();
        String rol = this.cmbRol.getValue();
        String estado = this.cmbEstado.getValue();

        String sql = "Update Usuario set nombre = '" + nombre + "', apellido ='" +
                apellido + "', nombre_usuario ='" + usuario + "', contrasena ='" +
                contrasena + "', rol ='" + rol + "', estado ='" + estado +
                "' where id_usuario=" + id + ";";

        ejecutarSQL(sql);
    }

    // =======================================================
    // 5. BORRAR (DELETE)
    // =======================================================
    public void fnBorrarUsuario(ActionEvent actionEvent) {
        String username = this.txtUsuario.getText().trim();
        if(username.isEmpty()) return;

        ButtonType bntSip = new ButtonType("Confirmar");
        ButtonType bntNop = new ButtonType("Cancelar");

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, "Se va a borrar el usuario '" + username + "' del sistema", bntSip, bntNop);
        Optional<ButtonType> resultado = alerta.showAndWait();

        if(resultado.isPresent() && resultado.get() == bntSip){
            String SQL = "Delete from Usuario where nombre_usuario ='" + username +"';";
            ejecutarSQL(SQL);
        }
    }

    // =======================================================
    // 6. UTILIDADES
    // =======================================================
    public void ejecutarSQL(String sql) {
        try (Connection connection = conexion.estabecerConexion();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            int result = preparedStatement.executeUpdate();
            if (result == 1){
                JOptionPane.showMessageDialog(null, "Acción ejecutada corrrectamente :DD");
                actualizarLista();
                limpiar();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.toString());
        }
    }

    @FXML
    public void fnLimpiarUsuario(ActionEvent actionEvent) {
        limpiar();
    }

    public void limpiar() {
        txtIdUsuario.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtUsuario.clear();
        txtContrasena.clear();
        cmbRol.setValue(null);
        cmbEstado.getSelectionModel().selectFirst();
    }
}