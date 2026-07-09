package com.microlib.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId; 
    private Long libroId;     

    private LocalDate fechaPrestamo;

    private String estado;
    private LocalDate fechaDevolucionReal;
}

