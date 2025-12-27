package com.example.demo.controller;

import com.example.demo.clases.Usuario;
import com.example.demo.interfaz.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class HomeController {

    private final UsuarioRepository usuarioRepository;

    public HomeController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal) {
        // Si no ha iniciado sesión, mostrar botón de Google
        if (principal == null) {
            return "<h1>Bienvenido</h1>" +
                    "<a href='/oauth2/authorization/google'>Iniciar sesión con Google</a>";
        }

        // 1. Extraer datos (sin mostrarlos en pantalla)
        String email = principal.getAttribute("email");
        String nombre = principal.getAttribute("name");
        String localizacion = principal.getAttribute("locale");
        String telefono = principal.getAttribute("phone_number");
        String direccion = principal.getAttribute("address");

        if (localizacion == null) localizacion = "Desconocida";
        if (telefono == null) telefono = "No registrado";
        if (direccion == null) direccion = "No registrada";

        // 2. Guardar en Base de Datos (Proceso interno)
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);

        if (usuarioExistente.isEmpty()) {
            Usuario nuevoUsuario = new Usuario(email, nombre, telefono, direccion, localizacion);
            usuarioRepository.save(nuevoUsuario);
            System.out.println("NUEVO USUARIO REGISTRADO: " + email);
        } else {
            Usuario u = usuarioExistente.get();
            u.setNombre(nombre); // Actualizamos nombre por si cambió
            // Solo actualizamos estos si Google mandó datos reales
            if (!"Desconocida".equals(localizacion)) u.setLocalizacion(localizacion);
            if (!"No registrado".equals(telefono)) u.setTelefono(telefono);
            usuarioRepository.save(u);
            System.out.println("USUARIO EXISTENTE ACTUALIZADO: " + email);
        }

        // 3. Mostrar PANTALLA DE ÉXITO (Limpia)
        return """
            <div style='text-align: center; font-family: Arial, sans-serif; margin-top: 50px;'>
                <h1 style='color: green;'>¡Registro Exitoso!</h1>
                <p style='font-size: 18px;'>Su registro fue exitoso y estamos procesando su factura.</p>
                <br>
                <hr style='width: 50%;'>
                <br>
                <a href='/logout' style='
                    background-color: #007bff;
                    color: white;
                    padding: 10px 20px;
                    text-decoration: none;
                    border-radius: 5px;
                    font-size: 16px;
                '>Volver a registrarse con otra cuenta</a>
            </div>
        """;
    }

    // Mantenemos este endpoint oculto por si tú (como admin) quieres ver los datos
    @GetMapping("/lista-usuarios")
    public String listarUsuarios() {
        StringBuilder html = new StringBuilder("<h1>Usuarios Registrados</h1><ul>");
        usuarioRepository.findAll().forEach(u -> {
            html.append("<li>")
                    .append(u.getNombre()).append(" - ").append(u.getEmail())
                    .append("</li>");
        });
        return html.toString();
    }
}