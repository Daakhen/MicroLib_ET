package com.microlib.controller;

import com.microlib.model.libro;
import com.microlib.repository.LibroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/libros")
public class LibroController {
    private final LibroRepository libroRepository;

    public LibroController(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }


    @GetMapping
    public List<libro> getAllLibros() {
        return libroRepository.findAll();
    }

    @GetMapping("/{id}")
    public libro buscarPorId(@PathVariable Long id) {
        return libroRepository.findById(id).orElse(null);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public libro gaurdadLibro(@RequestBody libro libro) {
        return libroRepository.save(libro);
    }
}
