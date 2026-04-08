package com.example.puestosypintado.modelo;

import javafx.beans.property.*;

public class Proveedor {
    private final StringProperty id_proveedor;
    private final StringProperty nombre_empresa;
    private final StringProperty nombre_contacto;
    private final StringProperty telefono_empresa;
    private final StringProperty categoria;

    // Campos para la tabla
    public Proveedor(String id, String empresa, String contacto, String telefono, String categoria) {
        this.id_proveedor = new SimpleStringProperty(id);
        this.nombre_empresa = new SimpleStringProperty(empresa);
        this.nombre_contacto = new SimpleStringProperty(contacto);
        this.telefono_empresa = new SimpleStringProperty(telefono);
        this.categoria = new SimpleStringProperty(categoria);
    }

    public String getId() { return id_proveedor.get(); }
    public String getEmpresa() { return nombre_empresa.get(); }
    public String getContacto() { return nombre_contacto.get(); }
    public String getTelefono() { return telefono_empresa.get(); }
    public String getCategoria() { return categoria.get(); }

    public StringProperty idProperty() { return id_proveedor; }
    public StringProperty empresaProperty() { return nombre_empresa; }
    public StringProperty contactoProperty() { return nombre_contacto; }
    public StringProperty telefonoProperty() { return telefono_empresa; }
    public StringProperty categoriaProperty() { return categoria; }
}