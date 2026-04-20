package com.example.puestosypintado.modelo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Poducto {
    private final StringProperty sku;
    private final StringProperty nombre;
    private final StringProperty stock;

    public Poducto(String sku, String nombre, String stock) {
        this.sku = new SimpleStringProperty(sku);
        this.nombre = new SimpleStringProperty(nombre);
        this.stock = new SimpleStringProperty(stock);
    }

    public String getSku() { return sku.get(); }
    public String getNombre() { return nombre.get(); }
    public String getStock() { return stock.get(); }

    public void setSku(String sku) { this.sku.set(sku); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public void setStock(String stock) { this.stock.set(stock); }

    public StringProperty skuProperty() { return sku; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty stockProperty() { return stock; }
}