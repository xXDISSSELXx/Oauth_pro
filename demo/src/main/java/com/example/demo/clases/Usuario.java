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

    // Constructores, Getters y Setters
    public Usuario() {}

    public Usuario(String email, String nombre) {
        this.email = email;
        this.nombre = nombre;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
