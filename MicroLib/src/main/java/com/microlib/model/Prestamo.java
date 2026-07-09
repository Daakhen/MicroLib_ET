package com.microlib.model;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prestamos")
@Getter
@Setter
@NoArgsConstructor      
@AllArgsConstructor
public class Prestamo {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private LocalDate fechaPrestamo;
    
    @Column(nullable = false)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "libro_id", nullable = false)
    private libro libro;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)  
    private Usuario usuario;

}

