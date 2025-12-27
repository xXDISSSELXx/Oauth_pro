package com.example.demo.controller;

import com.example.demo.clases.Usuario;
import com.example.demo.interfaz.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private final UsuarioRepository usuarioRepository;

    public HomeController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return "<h1>Bienvenido</h1> <a href='/oauth2/authorization/google'>Iniciar sesión con Google</a>";
        }

        // Extraer datos que Google nos envía
        String email = principal.getAttribute("email");
        String nombre = principal.getAttribute("name");

        // Lógica: Guardar en BD si no existe
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            Usuario nuevoUsuario = new Usuario(email, nombre);
            usuarioRepository.save(nuevoUsuario);
            System.out.println("NUEVO USUARIO REGISTRADO: " + email);
        } else {
            System.out.println("El usuario ya existía: " + email);
        }

        return "<h1>Hola, " + nombre + "</h1><p>Tu correo (" + email + ") ha sido registrado/verificado en nuestra base de datos.</p>";
    }
    // Añade este método dentro de HomeController.java
    @GetMapping("/lista-usuarios")
    public String listarUsuarios() {
        StringBuilder html = new StringBuilder("<h1>Usuarios Registrados</h1><ul>");

        // Obtenemos todos los usuarios de la base de datos
        usuarioRepository.findAll().forEach(u -> {
            html.append("<li>")
                    .append("<b>Nombre:</b> ").append(u.getNombre())
                    .append(" | <b>Email:</b> ").append(u.getEmail())
                    .append("</li>");
        });

        html.append("</ul><br><a href='/'>Volver al inicio</a>");
        return html.toString();
    }
}
