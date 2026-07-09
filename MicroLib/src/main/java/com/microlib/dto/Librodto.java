package com.microlib.dto;
import lombok.Data;

@Data
public class Librodto {
    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private Boolean disponible;
}
