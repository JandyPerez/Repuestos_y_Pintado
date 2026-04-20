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

    // ─── FXML ────────────────────────────────────────────────────
    @FXML private TextField        txtIdVehiculo;
    @FXML private TextField        txtMarca;
    @FXML private TextField        txtModelo;
    @FXML private TextField        txtAno;
    @FXML private TextField        txtColor;
    @FXML private TextField        txtPlaca;
    @FXML private TextField        txtVIN;
    @FXML private TextArea         txtNotas;
    @FXML private ComboBox<String> cmbCliente;

    @FXML private TableView<Vehiculo>           tvVehiculos;
    @FXML private TableColumn<Vehiculo, String> colPlaca;
    @FXML private TableColumn<Vehiculo, String> colMarcaModelo;

    // ─── INITIALIZE ──────────────────────────────────────────────
    public void initialize() {

        colPlaca.setCellValueFactory(
                c -> c.getValue().placaProperty());
        colMarcaModelo.setCellValueFactory(
                c -> c.getValue().marcaModeloProperty());

        cargarClientes();   // ← carga clientes reales desde la BD
        actualizarLista();

        // Clic en fila → rellena el formulario automáticamente
        tvVehiculos.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        buscarPorPlaca(newVal.getPlaca());
                    }
                });
    }

    // ─── CARGAR CLIENTES EN COMBOBOX ─────────────────────────────
    // FIX #11: antes estaba hardcodeado con un solo valor.
    // Ajusta los nombres de columna si tu tabla Clientes los tiene distintos.
    private void cargarClientes() {

        ObservableList<String> items = FXCollections.observableArrayList();

        String sql = "SELECT id_cliente, cedula, nombre FROM Clientes ORDER BY nombre";

        try (Connection conn = conexion.estabecerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Formato: "3 - 001-0000000-1 Juan Pérez"
                items.add(rs.getInt("id_cliente")
                        + " - "
                        + rs.getString("cedula")
                        + " "
                        + rs.getString("nombre"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al cargar clientes: " + e.getMessage());
        }

        cmbCliente.setItems(items);
    }

    // Extrae el id_cliente del ítem seleccionado ("3 - cedula nombre")
    private int getIdClienteSeleccionado() {
        String valor = cmbCliente.getValue();
        if (valor == null || valor.isBlank()) return -1;
        try {
            return Integer.parseInt(valor.split(" - ")[0].trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ─── LISTA (TableView) ───────────────────────────────────────
    protected ObservableList<Vehiculo> observableVehiculo() {

        ObservableList<Vehiculo> lista = FXCollections.observableArrayList();

        String sql = "SELECT Placa, CONCAT(Marca,' ',Modelo) AS MarcaModelo "
                + "FROM Vehiculos";                          // FIX #1: "Vehiculos" (con s)

        try (Connection conn = conexion.estabecerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Vehiculo(
                        rs.getString("Placa"),
                        rs.getString("MarcaModelo")));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al cargar lista: " + e.getMessage());
        }

        return lista;
    }

    public void actualizarLista() {
        tvVehiculos.setItems(observableVehiculo());
    }

    // ─── GUARDAR ─────────────────────────────────────────────────
    // FIX #1  : tabla "Vehiculos" (con s)
    // FIX #2  : incluye id_cliente (NOT NULL)
    // FIX #3  : columna "año"  (no "Ano")
    // FIX #4  : columna "Observaciones" (no "Notas")
    // FIX #9  : PreparedStatement con parámetros ? (sin concatenación)
    @FXML
    protected void fnGuardarVehiculo(ActionEvent event) {

        String placa    = txtPlaca.getText().trim();
        String marca    = txtMarca.getText().trim();
        String vin      = txtVIN.getText().trim();
        int    idCliente = getIdClienteSeleccionado();

        if (idCliente == -1 || placa.isEmpty() || marca.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Cliente, Marca y Placa son obligatorios.");
            return;
        }

        if (existeVehiculo(placa, vin)) {
            JOptionPane.showMessageDialog(null,
                    "Ya existe un vehículo con esa Placa o VIN.");
            return;
        }

        String sql = "INSERT INTO Vehiculos "                    // FIX #1 (tabla)
                + "(id_cliente, Marca, Modelo, año, Color, Placa, VIN, Observaciones) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";         // FIX #9 (parámetros)

        try (Connection conn = conexion.estabecerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt   (1, idCliente);                         // FIX #2 (id_cliente)
            ps.setString(2, marca);
            ps.setString(3, txtModelo.getText().trim());

            String anoStr = txtAno.getText().trim();
            if (!anoStr.isEmpty()) {
                ps.setInt(4, Integer.parseInt(anoStr));
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setString(5, txtColor.getText().trim());
            ps.setString(6, placa);
            ps.setString(7, vin.isEmpty() ? null : vin);
            ps.setString(8, txtNotas.getText().trim());         // FIX #4 → columna Observaciones

            if (ps.executeUpdate() == 1) {
                JOptionPane.showMessageDialog(null,
                        "Vehículo guardado correctamente.");
                actualizarLista();
                fnLimpiar(null);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "El campo AÑO debe contener solo números.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al guardar: " + e.getMessage());
        }
    }

    // Verifica duplicados de Placa o VIN antes de insertar
    private boolean existeVehiculo(String placa, String vin) {

        String sql = "SELECT id_vehiculo FROM Vehiculos "
                + "WHERE Placa = ? OR (VIN IS NOT NULL AND VIN = ?)";

        try (Connection conn = conexion.estabecerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, placa);
            ps.setString(2, vin.isEmpty() ? null : vin);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            return false;
        }
    }

    // ─── MODIFICAR ───────────────────────────────────────────────
    // FIX #5  : tabla "Vehiculos" (con s)
    // FIX #6  : PreparedStatement con parámetros ? (sin concatenación)
    // FIX #3  : columna "año"
    // FIX #4  : columna "Observaciones"
    @FXML
    public void fnModificarVehiculo(ActionEvent event) {

        if (txtIdVehiculo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Busque un vehículo primero.");
            return;
        }

        int idCliente = getIdClienteSeleccionado();
        if (idCliente == -1) {
            JOptionPane.showMessageDialog(null,
                    "Seleccione un cliente.");
            return;
        }

        String sql = "UPDATE Vehiculos SET "                    // FIX #5 (tabla)
                + "id_cliente=?, Marca=?, Modelo=?, año=?, " // FIX #3 (columna año)
                + "Color=?, Placa=?, VIN=?, Observaciones=? "// FIX #4 (columna Observaciones)
                + "WHERE id_vehiculo=?";                     // FIX #6 (parámetros)

        try (Connection conn = conexion.estabecerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt   (1, idCliente);
            ps.setString(2, txtMarca.getText().trim());
            ps.setString(3, txtModelo.getText().trim());

            String anoStr = txtAno.getText().trim();
            if (!anoStr.isEmpty()) {
                ps.setInt(4, Integer.parseInt(anoStr));
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setString(5, txtColor.getText().trim());
            ps.setString(6, txtPlaca.getText().trim());
            ps.setString(7, txtVIN.getText().trim());
            ps.setString(8, txtNotas.getText().trim());
            ps.setInt   (9, Integer.parseInt(txtIdVehiculo.getText().trim()));

            if (ps.executeUpdate() == 1) {
                JOptionPane.showMessageDialog(null,
                        "Vehículo modificado correctamente.");
                actualizarLista();
                fnLimpiar(null);
            } else {
                JOptionPane.showMessageDialog(null,
                        "No se encontró el vehículo para modificar.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "El campo AÑO debe contener solo números.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al modificar: " + e.getMessage());
        }
    }

    // ─── BORRAR ──────────────────────────────────────────────────
    // FIX #7: PreparedStatement con parámetro ? (sin concatenación)
    @FXML
    public void fnBorrarVehiculo(ActionEvent event) {

        String placa = txtPlaca.getText().trim();
        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Busque un vehículo primero.");
            return;
        }

        Alert alerta = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Seguro que desea borrar el vehículo con placa " + placa + "?",
                ButtonType.YES,
                ButtonType.NO);
        alerta.setTitle("Confirmar eliminación");

        Optional<ButtonType> res = alerta.showAndWait();

        if (res.isPresent() && res.get() == ButtonType.YES) {

            String sql = "DELETE FROM Vehiculos WHERE Placa = ?"; // FIX #7 (parámetro)

            try (Connection conn = conexion.estabecerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, placa);

                if (ps.executeUpdate() == 1) {
                    JOptionPane.showMessageDialog(null,
                            "Vehículo eliminado correctamente.");
                    actualizarLista();
                    fnLimpiar(null);
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                        "Error al borrar: " + e.getMessage());
            }
        }
    }

    // ─── BUSCAR (botón 🔍 junto a txtIdVehiculo) ─────────────────
    // FIX #8  : PreparedStatement con parámetro ? (sin concatenación)
    // FIX #12 : El botón está junto al ID, por tanto busca por id_vehiculo
    @FXML
    public void fnBuscarVehiculo(ActionEvent event) {

        String idStr = txtIdVehiculo.getText().trim();

        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Ingrese un ID de vehículo para buscar.");
            return;
        }

        try {
            buscarPorId(Integer.parseInt(idStr));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "El ID debe ser un número entero.");
        }
    }

    // Busca por id_vehiculo  (botón 🔍)
    private void buscarPorId(int id) {

        String sql = "SELECT * FROM Vehiculos WHERE id_vehiculo = ?"; // FIX #8 (parámetro)

        try (Connection conn = conexion.estabecerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                rellenarFormulario(rs);
            } else {
                JOptionPane.showMessageDialog(null,
                        "No se encontró ningún vehículo con ese ID.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al buscar: " + e.getMessage());
        }
    }

    // Busca por Placa  (clic en fila de la tabla)
    private void buscarPorPlaca(String placa) {

        String sql = "SELECT * FROM Vehiculos WHERE Placa = ?";

        try (Connection conn = conexion.estabecerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, placa);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) rellenarFormulario(rs);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al buscar: " + e.getMessage());
        }
    }

    // Rellena todos los campos del formulario desde un ResultSet
    // FIX #3/#4: usa nombres de columna reales (año, Observaciones)
    private void rellenarFormulario(ResultSet rs) throws SQLException {

        txtIdVehiculo.setText(rs.getString("id_vehiculo"));
        txtMarca.setText     (rs.getString("Marca"));
        txtModelo.setText    (rs.getString("Modelo"));
        txtAno.setText       (rs.getString("año"));             // FIX #3
        txtColor.setText     (rs.getString("Color"));
        txtPlaca.setText     (rs.getString("Placa"));
        txtVIN.setText       (rs.getString("VIN"));
        txtNotas.setText     (rs.getString("Observaciones"));   // FIX #4

        // Selecciona el cliente correcto en el ComboBox
        int idCliente = rs.getInt("id_cliente");
        cmbCliente.getItems().stream()
                .filter(item -> item.startsWith(idCliente + " - "))
                .findFirst()
                .ifPresent(cmbCliente::setValue);
    }

    // ─── LIMPIAR ─────────────────────────────────────────────────
    @FXML
    public void fnLimpiar(ActionEvent event) {
        txtIdVehiculo.clear();
        txtMarca.clear();
        txtModelo.clear();
        txtAno.clear();
        txtColor.clear();
        txtPlaca.clear();
        txtVIN.clear();
        txtNotas.clear();
        cmbCliente.setValue(null);
        tvVehiculos.getSelectionModel().clearSelection();
    }
}