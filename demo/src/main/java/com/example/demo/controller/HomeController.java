package com.example.demo.controller;

import com.example.demo.clases.Factura;
import com.example.demo.clases.Usuario;
import com.example.demo.interfaz.FacturaRepository;
import com.example.demo.interfaz.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class HomeController {

    private final UsuarioRepository usuarioRepository;
    private final FacturaRepository facturaRepository;

    public HomeController(UsuarioRepository usuarioRepository, FacturaRepository facturaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.facturaRepository = facturaRepository;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return "<h1>Bienvenido</h1> <a href='/oauth2/authorization/google'>Iniciar sesión con Google</a>";
        }

        // 1. Obtener datos de Google para pre-llenar el formulario
        String email = principal.getAttribute("email");
        String nombre = principal.getAttribute("name");

        // Guardamos/Actualizamos el usuario silenciosamente
        guardarUsuarioSilencioso(principal, email, nombre);

        // 2. Retornar el Formulario con Diseño Apple UI (CORREGIDO: %% en lugar de %)
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Registro de Factura</title>
                <style>
                    :root {
                        --bg-color: #fbfbfd;
                        --card-bg: #ffffff;
                        --text-primary: #1d1d1f;
                        --text-secondary: #86868b;
                        --accent-color: #0071e3;
                        --accent-hover: #0077ed;
                        --input-border: #d2d2d7;
                        --input-focus: #0071e3;
                    }
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                        background-color: var(--bg-color);
                        color: var(--text-primary);
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        min-height: 100vh;
                        margin: 0;
                    }
                    .container {
                        background-color: var(--card-bg);
                        padding: 40px;
                        border-radius: 18px;
                        box-shadow: 0 4px 20px rgba(0,0,0,0.08);
                        width: 100%%; /* <--- AQUÍ ESTABA EL ERROR, AHORA ES %% */
                        max-width: 480px;
                        text-align: center;
                    }
                    h1 {
                        font-size: 28px;
                        font-weight: 600;
                        margin-bottom: 10px;
                    }
                    p.subtitle {
                        color: var(--text-secondary);
                        font-size: 15px;
                        margin-bottom: 30px;
                    }
                    .form-group {
                        text-align: left;
                        margin-bottom: 20px;
                    }
                    label {
                        display: block;
                        font-size: 13px;
                        font-weight: 500;
                        margin-bottom: 8px;
                        color: var(--text-primary);
                    }
                    input, select {
                        width: 100%%; /* <--- CORREGIDO */
                        padding: 12px 10px;
                        font-size: 16px;
                        border: 1px solid var(--input-border);
                        border-radius: 10px;
                        box-sizing: border-box;
                        transition: all 0.2s ease;
                        font-family: inherit;
                        background-color: #fff;
                        outline: none;
                    }
                    input:focus, select:focus {
                        border-color: var(--input-focus);
                        box-shadow: 0 0 0 3px rgba(0,113,227, 0.15);
                    }
                    .btn-submit {
                        background-color: var(--accent-color);
                        color: white;
                        border: none;
                        padding: 15px 30px;
                        font-size: 17px;
                        font-weight: 500;
                        border-radius: 99px;
                        cursor: pointer;
                        width: 100%%; /* <--- CORREGIDO */
                        margin-top: 10px;
                        transition: background-color 0.2s;
                    }
                    .btn-submit:hover {
                        background-color: var(--accent-hover);
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Factura N°89008</h1>
                    <p class="subtitle">Complete los datos para generar su factura electrónica.</p>
                    
                    <form action="/procesar-factura" method="POST">
                        
                        <div class="form-group">
                            <label>Nombre Completo</label>
                            <input type="text" name="nombre" value="%s" required> </div>

                        <div class="form-group">
                            <label>C.C / Identificación</label>
                            <input type="text" name="cedula" placeholder="Ej: 1030555000" required>
                        </div>

                        <div class="form-group">
                            <label>Correo Electrónico</label>
                            <input type="email" name="email" value="%s" required> </div>

                        <div class="form-group">
                            <label>Teléfono</label>
                            <input type="tel" name="telefono" placeholder="+57 300..." required>
                        </div>

                        <div class="form-group">
                            <label>Método de Pago</label>
                            <select name="metodoPago" required>
                                <option value="" disabled selected>Seleccione uno...</option>
                                <option value="Bancolombia">Bancolombia </option>
                                <option value="Transferencia PSE">Transferencia PSE</option>
                                <option value="Nequi / Daviplata">Nequi / Daviplata</option>
                                
                                <option value="Efectivo">Efectivo</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label>Valor a Pagar ($)</label>
                            <input type="number" name="valor" step="0.01" placeholder="0.00" required>
                        </div>

                        <button type="submit" class="btn-submit">Enviar y Registrar</button>
                    </form>
                </div>
            </body>
            </html>
        """.formatted(nombre, email);
    }

    @PostMapping("/procesar-factura")
    public String procesarFactura(
            @RequestParam String nombre,
            @RequestParam String cedula,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String metodoPago,
            @RequestParam Double valor
    ) {
        // Guardar la factura en la base de datos
        Factura nuevaFactura = new Factura(nombre, cedula, email, telefono, metodoPago, valor);
        facturaRepository.save(nuevaFactura);

        // Mostrar pantalla de éxito
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                        background-color: #fbfbfd;
                        margin: 0;
                        text-align: center;
                    }
                    .success-card {
                        background: white;
                        padding: 50px;
                        border-radius: 18px;
                        box-shadow: 0 4px 20px rgba(0,0,0,0.08);
                        max-width: 400px;
                    }
                    h1 { color: #1d1d1f; margin-bottom: 10px; }
                    p { color: #86868b; font-size: 18px; margin-bottom: 30px; }
                    .check-icon {
                        font-size: 60px;
                        color: #34c759;
                        margin-bottom: 20px;
                    }
                    .btn-link {
                        color: #0071e3;
                        text-decoration: none;
                        font-weight: 500;
                    }
                </style>
            </head>
            <body>
                <div class="success-card">
                    <div class="check-icon">✓</div>
                    <h1>¡Todo listo!</h1>
                    <p>Registro de factura exitosa.</p>
                    <a href="/logout" class="btn-link">Salir o registrar nueva cuenta</a>
                </div>
            </body>
            </html>
        """;
    }

    private void guardarUsuarioSilencioso(OAuth2User principal, String email, String nombre) {
        String localizacion = principal.getAttribute("locale");
        if (localizacion == null) localizacion = "Desconocida";

        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);
        if (usuarioExistente.isEmpty()) {
            Usuario nuevoUsuario = new Usuario(email, nombre, "No registrado", "No registrada", localizacion);
            usuarioRepository.save(nuevoUsuario);
        }
    }
}