package com.microlib.service;

import com.microlib.dto.UsuarioDTO;
import com.microlib.model.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioCargaMasivaService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public String cargaMasiva(List<UsuarioDTO> usuarios) {

        long inicio = System.currentTimeMillis();
        int batchSize = 50;

        for (int i = 0; i < usuarios.size(); i++) {

            UsuarioDTO dto = usuarios.get(i);

            Usuario usuario = new Usuario();
            usuario.setNombre(dto.getNombre());
            usuario.setCorreo(dto.getCorreo());

            entityManager.persist(usuario);

            if (i % batchSize == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        long fin = System.currentTimeMillis();

        return "Usuarios insertados: " + usuarios.size() +
               " | Tiempo: " + (fin - inicio) + " ms";
    }
}