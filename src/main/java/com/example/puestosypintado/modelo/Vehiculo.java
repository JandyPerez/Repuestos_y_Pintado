package com.example.puestosypintado.modelo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Vehiculo {
    private final StringProperty placa;
    private final StringProperty marcaModelo;

    public Vehiculo(String placa, String marcaModelo) {
        this.placa = new SimpleStringProperty(placa);
        this.marcaModelo = new SimpleStringProperty(marcaModelo);
    }

    public String getPlaca() { return placa.get(); }
    public String getMarcaModelo() { return marcaModelo.get(); }

    public void setPlaca(String placa) { this.placa.set(placa); }
    public void setMarcaModelo(String marcaModelo) { this.marcaModelo.set(marcaModelo); }

    public StringProperty placaProperty() { return placa; }
    public StringProperty marcaModeloProperty() { return marcaModelo; }
}