
package com.microlib.controller;

import com.microlib.model.libro;
import com.microlib.repository.LibroRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/libros")
@Tag(name = "Libros", description = "Gestión de libros de la biblioteca (libro_ms)")
public class LibroController {

    private final LibroRepository libroRepository;

    public LibroController(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    // ── GET /libros ────────────────────────────────────────────────────────
    @GetMapping
    @Operation(summary = "Listar todos los libros")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public CollectionModel<EntityModel<libro>> getAllLibros() {

        List<EntityModel<libro>> libros = libroRepository.findAll().stream()
            .map(lib -> EntityModel.of(lib,
                linkTo(methodOn(LibroController.class).buscarPorId(lib.getId())).withSelfRel(),
                linkTo(methodOn(LibroController.class).getAllLibros()).withRel("libros")
            ))
            .collect(Collectors.toList());

        return CollectionModel.of(libros,
            linkTo(methodOn(LibroController.class).getAllLibros()).withSelfRel()
        );
    }

    // ── GET /libros/{id} ───────────────────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Buscar libro por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Libro encontrado"),
        @ApiResponse(responseCode = "404", description = "Libro no encontrado")
    })
    public EntityModel<libro> buscarPorId(
            @Parameter(description = "ID del libro") @PathVariable Long id) {

        libro lib = libroRepository.findById(id).orElse(null);
        if (lib == null) return null;

        return EntityModel.of(lib,
            linkTo(methodOn(LibroController.class).buscarPorId(id)).withSelfRel(),
            linkTo(methodOn(LibroController.class).getAllLibros()).withRel("libros"),
            // link a la acción de actualizar estado
            linkTo(methodOn(LibroController.class).actualizarEstado(id, null)).withRel("actualizar-estado")
        );
    }

    // ── POST /libros ───────────────────────────────────────────────────────
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo libro")
    @ApiResponse(responseCode = "201", description = "Libro creado exitosamente")
    public EntityModel<libro> gaurdadLibro(@RequestBody libro libro) {
        libro saved = libroRepository.save(libro);

        return EntityModel.of(saved,
            linkTo(methodOn(LibroController.class).buscarPorId(saved.getId())).withSelfRel(),
            linkTo(methodOn(LibroController.class).getAllLibros()).withRel("libros")
        );
    }

    // ── PUT /libros/{id}/estado ────────────────────────────────────────────
    @PutMapping("{id}/estado")
    @Operation(summary = "Actualizar disponibilidad de un libro",
               description = "Usado internamente por prestamo_ms al crear/devolver préstamos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "404", description = "Libro no encontrado")
    })
    public EntityModel<libro> actualizarEstado(
            @Parameter(description = "ID del libro") @PathVariable Long id,
            @RequestBody libro libroActualizado) {

        libro libro = libroRepository.findById(id).orElse(null);
        if (libro == null) return null;

        libro.setDisponible(libroActualizado.getDisponible());
        libro saved = libroRepository.save(libro);

        return EntityModel.of(saved,
            linkTo(methodOn(LibroController.class).buscarPorId(id)).withSelfRel(),
            linkTo(methodOn(LibroController.class).getAllLibros()).withRel("libros")
        );
    }
}
