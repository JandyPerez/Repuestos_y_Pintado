package com.example.puestosypintado.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomeController {

    private void cambiarEscena(ActionEvent event, String rutaFXML) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(rutaFXML));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void irCliente(ActionEvent event) {
        cambiarEscena(event, "/com/example/puestosypintado/Cliente.fxml");
    }

    public void irEmpleado(ActionEvent event) {
        cambiarEscena(event, "/com/example/puestosypintado/Empleado.fxml");
    }

    public void irProducto(ActionEvent event) {
        cambiarEscena(event, "/com/example/puestosypintado/Producto.fxml");
    }

    public void irProveedor(ActionEvent event) {
        cambiarEscena(event, "/com/example/puestosypintado/Proveedor.fxml");
    }

    public void irUsuario(ActionEvent event) {
        cambiarEscena(event, "/com/example/puestosypintado/Usuario.fxml");
    }

    public void irVehiculo(ActionEvent event) {
        cambiarEscena(event, "/com/example/puestosypintado/Vehiculo.fxml");
    }
}