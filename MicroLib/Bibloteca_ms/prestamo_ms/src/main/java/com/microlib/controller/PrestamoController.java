package com.microlib.controller;

import com.microlib.client.LibroClient;
import com.microlib.client.UsuarioClient;
import com.microlib.dto.LibroDTO;
import com.microlib.dto.PrestamoRequestDTO;
import com.microlib.dto.UsuarioDTO;
import com.microlib.model.Prestamo;
import com.microlib.repository.PrestamoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/prestamos")
@Tag(name = "Préstamos", description = "Gestión de préstamos (prestamo_ms). Se comunica con libro_ms y usuario_ms vía Feign.")
public class PrestamoController {

    private final PrestamoRepository prestamoRepository;
    private final LibroClient libroClient;
    private final UsuarioClient usuarioClient;

    public PrestamoController(PrestamoRepository prestamoRepository,
                              LibroClient libroClient,
                              UsuarioClient usuarioClient) {
        this.prestamoRepository = prestamoRepository;
        this.libroClient = libroClient;
        this.usuarioClient = usuarioClient;
    }

    // ── GET /prestamos ─────────────────────────────────────────────────────
    @GetMapping
    @Operation(summary = "Listar todos los préstamos")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public CollectionModel<EntityModel<Prestamo>> listarPrestamos() {

        List<EntityModel<Prestamo>> prestamos = prestamoRepository.findAll().stream()
            .map(this::toEntityModel)
            .collect(Collectors.toList());

        return CollectionModel.of(prestamos,
            linkTo(methodOn(PrestamoController.class).listarPrestamos()).withSelfRel()
        );
    }

    // ── GET /prestamos/{id} ────────────────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Buscar préstamo por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Préstamo encontrado"),
        @ApiResponse(responseCode = "404", description = "Préstamo no encontrado")
    })
    public EntityModel<Prestamo> buscarPorId(
            @Parameter(description = "ID del préstamo") @PathVariable Long id) {

        Prestamo prestamo = prestamoRepository.findById(id).orElse(null);
        if (prestamo == null) return null;

        return toEntityModel(prestamo);
    }

    // ── POST /prestamos ────────────────────────────────────────────────────
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo préstamo",
               description = "Valida disponibilidad del libro vía libro_ms y existencia del usuario vía usuario_ms. " +
                             "Marca el libro como no disponible.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Préstamo creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Libro/usuario no encontrado o libro no disponible")
    })
    public EntityModel<Prestamo> crearPrestamo(@RequestBody PrestamoRequestDTO request) {

        UsuarioDTO usuario = usuarioClient.obtenerUsuario(request.getUsuarioId());
        LibroDTO libro = libroClient.obtenerLibro(request.getLibroId());

        if (usuario == null || libro == null) {
            return null;
        }

        if (Boolean.FALSE.equals(libro.getDisponible())) {
            return null;
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuarioId(usuario.getId());
        prestamo.setLibroId(libro.getId());
        prestamo.setFechaPrestamo(request.getFechaPrestamo());
        prestamo.setEstado("ACTIVO");

        libro.setDisponible(false);
        libroClient.actualizarEstado(libro.getId(), libro);

        Prestamo saved = prestamoRepository.save(prestamo);
        return toEntityModel(saved);
    }

    // ── PUT /prestamos/{id}/devolver ───────────────────────────────────────
    @PutMapping("/{id}/devolver")
    @Operation(summary = "Registrar la devolución de un préstamo",
               description = "Marca el préstamo como DEVUELTO y vuelve a habilitar el libro en libro_ms.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Devolución registrada"),
        @ApiResponse(responseCode = "404", description = "Préstamo no encontrado")
    })
    public EntityModel<Prestamo> devolverPrestamo(
            @Parameter(description = "ID del préstamo") @PathVariable Long id) {

        Prestamo prestamo = prestamoRepository.findById(id).orElse(null);
        if (prestamo == null) return null;

        prestamo.setEstado("DEVUELTO");
        prestamo.setFechaDevolucionReal(java.time.LocalDate.now());

        LibroDTO libro = libroClient.obtenerLibro(prestamo.getLibroId());
        libro.setDisponible(true);
        libroClient.actualizarEstado(libro.getId(), libro);

        Prestamo saved = prestamoRepository.save(prestamo);
        return toEntityModel(saved);
    }

    // ── Helper: construye los links HATEOAS, incluyendo cross-service ──────
    private EntityModel<Prestamo> toEntityModel(Prestamo prestamo) {

        EntityModel<Prestamo> model = EntityModel.of(prestamo,
            // self
            linkTo(methodOn(PrestamoController.class).buscarPorId(prestamo.getId())).withSelfRel(),
            // colección
            linkTo(methodOn(PrestamoController.class).listarPrestamos()).withRel("prestamos")
        );

        // link cross-service hacia el libro en libro_ms (puerto 8081)
        model.add(Link.of("http://localhost:8081/libros/" + prestamo.getLibroId(), "libro"));

        // link cross-service hacia el usuario en usuario_ms (puerto 8082)
        model.add(Link.of("http://localhost:8082/usuarios/" + prestamo.getUsuarioId(), "usuario"));

        // si el préstamo sigue activo, ofrecer la acción de devolver
        if ("ACTIVO".equals(prestamo.getEstado())) {
            model.add(linkTo(methodOn(PrestamoController.class).devolverPrestamo(prestamo.getId())).withRel("devolver"));
        }

        return model;
    }
}
