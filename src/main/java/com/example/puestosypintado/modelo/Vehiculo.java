package com.example.puestosypintado.modelo;

import javafx.beans.property.*;

/**
 * Modelo completo del Vehículo.
 * Contiene todas las columnas de la tabla Vehiculos
 * más la propiedad calculada marcaModelo para la TableView.
 */
public class Vehiculo {

    // ─── Propiedades ────────────────────────────────────────────
    private final IntegerProperty idVehiculo    = new SimpleIntegerProperty();
    private final IntegerProperty idCliente     = new SimpleIntegerProperty();
    private final StringProperty  marca         = new SimpleStringProperty();
    private final StringProperty  modelo        = new SimpleStringProperty();
    private final IntegerProperty ano           = new SimpleIntegerProperty();
    private final StringProperty  color         = new SimpleStringProperty();
    private final StringProperty  placa         = new SimpleStringProperty();
    private final StringProperty  vin           = new SimpleStringProperty();
    private final StringProperty  observaciones = new SimpleStringProperty();

    // Propiedad calculada: se usa en la columna "MARCA / MODELO" del TableView
    private final StringProperty  marcaModelo   = new SimpleStringProperty();

    // ─── Constructor para TableView (SELECT lista) ───────────────
    public Vehiculo(String placa, String marcaModelo) {
        this.placa.set(placa);
        this.marcaModelo.set(marcaModelo);
    }

    // ─── Constructor completo (para llenar formulario) ───────────
    public Vehiculo(int idVehiculo, int idCliente,
                    String marca, String modelo, int ano,
                    String color, String placa, String vin,
                    String observaciones) {

        this.idVehiculo.set(idVehiculo);
        this.idCliente.set(idCliente);
        this.marca.set(marca);
        this.modelo.set(modelo);
        this.ano.set(ano);
        this.color.set(color);
        this.placa.set(placa);
        this.vin.set(vin);
        this.observaciones.set(observaciones);
        this.marcaModelo.set(marca + " " + modelo);
    }

    // ─── Getters ─────────────────────────────────────────────────
    public int    getIdVehiculo()    { return idVehiculo.get(); }
    public int    getIdCliente()     { return idCliente.get(); }
    public String getMarca()         { return marca.get(); }
    public String getModelo()        { return modelo.get(); }
    public int    getAno()           { return ano.get(); }
    public String getColor()         { return color.get(); }
    public String getPlaca()         { return placa.get(); }
    public String getVin()           { return vin.get(); }
    public String getObservaciones() { return observaciones.get(); }
    public String getMarcaModelo()   { return marcaModelo.get(); }

    // ─── Setters ─────────────────────────────────────────────────
    public void setIdVehiculo   (int    v) { idVehiculo.set(v); }
    public void setIdCliente    (int    v) { idCliente.set(v); }
    public void setMarca        (String v) { marca.set(v); }
    public void setModelo       (String v) { modelo.set(v); }
    public void setAno          (int    v) { ano.set(v); }
    public void setColor        (String v) { color.set(v); }
    public void setPlaca        (String v) { placa.set(v); marcaModelo.set(marca.get() + " " + modelo.get()); }
    public void setVin          (String v) { vin.set(v); }
    public void setObservaciones(String v) { observaciones.set(v); }
    public void setMarcaModelo  (String v) { marcaModelo.set(v); }

    // ─── Properties (para TableView / Binding) ───────────────────
    public IntegerProperty idVehiculoProperty()    { return idVehiculo; }
    public IntegerProperty idClienteProperty()     { return idCliente; }
    public StringProperty  marcaProperty()         { return marca; }
    public StringProperty  modeloProperty()        { return modelo; }
    public IntegerProperty anoProperty()           { return ano; }
    public StringProperty  colorProperty()         { return color; }
    public StringProperty  placaProperty()         { return placa; }
    public StringProperty  vinProperty()           { return vin; }
    public StringProperty  observacionesProperty() { return observaciones; }
    public StringProperty  marcaModeloProperty()   { return marcaModelo; }
}