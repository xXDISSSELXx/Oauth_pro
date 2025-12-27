package com.example.demo.clases;
import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String nombre;

    // 1. Nuevos campos
    private String telefono;
    private String direccion;
    private String localizacion;

    public Usuario() {}

    // 2. Constructor actualizado
    public Usuario(String email, String nombre, String telefono, String direccion, String localizacion) {
        this.email = email;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.localizacion = localizacion;
    }

    // 3. Getters y Setters nuevos
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getLocalizacion() { return localizacion; }
    public void setLocalizacion(String localizacion) { this.localizacion = localizacion; }
}