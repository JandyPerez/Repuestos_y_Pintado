package com.example.puestosypintado.modelo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Material {
    private final StringProperty sku;
    private final StringProperty descripcion;
    private final IntegerProperty stock;

    public Material(String sku, String descripcion, int stock) {
        this.sku = new SimpleStringProperty(sku);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.stock = new SimpleIntegerProperty(stock);
    }

    public String getSku() { return sku.get(); }
    public String getDescripcion() { return descripcion.get(); }
    public int getStock() { return stock.get(); }

    public void setSku(String sku) { this.sku.set(sku); }
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }
    public void setStock(int stock) { this.stock.set(stock); }

    public StringProperty skuProperty() { return sku; }
    public StringProperty descripcionProperty() { return descripcion; }
    public IntegerProperty stockProperty() { return stock; }
}