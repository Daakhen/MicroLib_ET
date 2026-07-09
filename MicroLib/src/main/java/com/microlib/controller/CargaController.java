package com.microlib.controller;
import com.microlib.dto.Librodto;
import com.microlib.repository.LibroRepository;
import com.microlib.service.CargaMasivaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/libros")
public class CargaController {
    
    @Autowired
    private CargaMasivaService service;
    @Autowired
    private LibroRepository libroRepository;

    @PostMapping("/masivo")
    public ResponseEntity<?> cargar(@RequestBody List<Librodto> libros) {
        try {
            if (libros == null || libros.isEmpty()) {
                return ResponseEntity.badRequest().body("La lista está vacía");
            }

            long inicio = System.currentTimeMillis();
            service.procesarCarga(libros);
            long fin = System.currentTimeMillis();

            return ResponseEntity.ok("Éxito: " + libros.size() + " procesados en " + (fin - inicio) + "ms");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en la carga: " + e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return libroRepository.findById(id)
        .map(libro -> ResponseEntity.ok(libro))
        .orElse(ResponseEntity.notFound().build());
    }
}