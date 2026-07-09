package com.microlib.controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.microlib.repository.UsuarioRepository;
import com.microlib.model.Usuario;


@RestController
@RequestMapping("/Usuario")

public class UsuarioController {
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;}


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario guardarUsuario(@RequestBody Usuario usuario){
        return usuarioRepository.save(usuario);

    }
}
