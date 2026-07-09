package com.microlib.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.microlib.service.UsuarioCargaMasivaService;
import java.util.List;
import com.microlib.dto.UsuarioDTO;
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioCargaMasivaController {

    private final UsuarioCargaMasivaService service;

    public UsuarioCargaMasivaController(UsuarioCargaMasivaService service) {
        this.service = service;
    }

    @PostMapping("/masivo")
    public ResponseEntity<String> cargaMasiva(@RequestBody List<UsuarioDTO> usuarios) {
        return ResponseEntity.ok(service.cargaMasiva(usuarios));
    }
}