package com.microlib.controller;
import com.microlib.model.libro;
import com.microlib.model.Prestamo;
import com.microlib.model.Usuario;
import com.microlib.repository.LibroRepository;
import com.microlib.repository.PrestamoRepository;
import com.microlib.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/prestamos")

public class PrestamoController {
    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;

    public PrestamoController(PrestamoRepository prestamoRepository, LibroRepository libroRepository, UsuarioRepository usuarioRepository) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
        this.usuarioRepository = usuarioRepository;
    }

        @GetMapping
    public List<Prestamo> listarPrestamos() {
        return prestamoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Prestamo buscarPorId(@PathVariable Long id) {
        return prestamoRepository.findById(id).orElse(null);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Prestamo crearPrestamo(@RequestBody PrestamoRequest request) {
        libro libro = libroRepository.findById(request.getIdLibro()).orElse(null);
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario()).orElse(null);

        if (libro == null || usuario == null) {
            return null;
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setFechaPrestamo(request.getFechaPrestamo());
        prestamo.setEstado(request.getEstado());
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);

        return prestamoRepository.save(prestamo);
    }

    public static class PrestamoRequest {
        private Long idLibro;
        private Long idUsuario;
        private LocalDate fechaPrestamo;
        private String estado;

        public Long getIdLibro() {
            return idLibro;
        }

        public void setIdLibro(Long idLibro) {
            this.idLibro = idLibro;
        }

        public Long getIdUsuario() {
            return idUsuario;
        }

        public void setIdUsuario(Long idUsuario) {
            this.idUsuario = idUsuario;
        }

        public LocalDate getFechaPrestamo() {
            return fechaPrestamo;
        }

        public void setFechaPrestamo(LocalDate fechaPrestamo) {
            this.fechaPrestamo = fechaPrestamo;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }
    }
}
