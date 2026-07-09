package com.microlib.client;

import com.microlib.dto.LibroDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "libro-ms", url = "http://localhost:8081")
public interface LibroClient {

    @GetMapping("/libros/{id}")
    LibroDTO obtenerLibro(@PathVariable("id") Long id);

    @PutMapping("/libros/{id}/estado")
    LibroDTO actualizarEstado(@PathVariable("id") Long id, @RequestBody LibroDTO libro);
}