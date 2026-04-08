package com.example.puestosypintado.modelo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cliente {
    private final StringProperty cedula;
    private final StringProperty nombre;

    public Cliente(String cedula, String nombre) {
        this.cedula = new SimpleStringProperty(cedula);
        this.nombre = new SimpleStringProperty(nombre);
    }

    // Getters estándar
    public String getCedula() { return cedula.get(); }
    public String getNombre() { return nombre.get(); }

    // Setters estándar
    public void setCedula(String cedula) { this.cedula.set(cedula); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }

    // Propiedades para el TableView (¡Esto es lo que usa el cellValueFactory!)
    public StringProperty cedulaProperty() { return cedula; }
    public StringProperty nombreProperty() { return nombre; }
}