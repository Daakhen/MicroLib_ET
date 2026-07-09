package com.microlib.service;
import com.microlib.dto.Librodto;
import com.microlib.model.libro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
public class CargaMasivaService {

    
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void procesarCarga(List<Librodto> listaDto) {
        int batchSize = 50;
        for (int i = 0; i < listaDto.size(); i++) {
            Librodto dto = listaDto.get(i);
            
            libro libro = new libro();
            libro.setId(dto.getId());
            libro.setTitulo(dto.getTitulo());
            libro.setAutor(dto.getAutor());
            libro.setIsbn(dto.getIsbn());
            libro.setDisponible(dto.getDisponible());

            entityManager.persist(libro);

            // Cada 50 registros, enviamos a la BD y limpiamos RAM
            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
}
