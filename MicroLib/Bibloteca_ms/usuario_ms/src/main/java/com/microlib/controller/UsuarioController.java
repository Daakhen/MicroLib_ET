package com.microlib.controller;

import com.microlib.model.Usuario;
import com.microlib.repository.UsuarioRepository;
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
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios de la biblioteca (usuario_ms)")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ── POST /usuarios ─────────────────────────────────────────────────────
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    public EntityModel<Usuario> guardarUsuario(@RequestBody Usuario usuario) {
        Usuario saved = usuarioRepository.save(usuario);

        return EntityModel.of(saved,
            linkTo(methodOn(UsuarioController.class).buscarPorId(saved.getId())).withSelfRel(),
            linkTo(methodOn(UsuarioController.class).getAllUsuarios()).withRel("usuarios")
        );
    }

    // ── GET /usuarios/{id} ─────────────────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public EntityModel<Usuario> buscarPorId(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {

        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) return null;

        return EntityModel.of(usuario,
            linkTo(methodOn(UsuarioController.class).buscarPorId(id)).withSelfRel(),
            linkTo(methodOn(UsuarioController.class).getAllUsuarios()).withRel("usuarios")
        );
    }

    // ── GET /usuarios ──────────────────────────────────────────────────────
    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public CollectionModel<EntityModel<Usuario>> getAllUsuarios() {

        List<EntityModel<Usuario>> usuarios = usuarioRepository.findAll().stream()
            .map(u -> EntityModel.of(u,
                linkTo(methodOn(UsuarioController.class).buscarPorId(u.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).getAllUsuarios()).withRel("usuarios")
            ))
            .collect(Collectors.toList());

        return CollectionModel.of(usuarios,
            linkTo(methodOn(UsuarioController.class).getAllUsuarios()).withSelfRel()
        );
    }
}
