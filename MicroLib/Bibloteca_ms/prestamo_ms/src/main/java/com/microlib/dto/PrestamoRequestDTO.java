package com.microlib.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PrestamoRequestDTO {
    private Long usuarioId;
    private Long libroId;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
}