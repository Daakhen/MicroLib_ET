package com.microlib;

import com.microlib.client.LibroClient;
import com.microlib.client.UsuarioClient;
import com.microlib.dto.LibroDTO;
import com.microlib.dto.UsuarioDTO;
import com.microlib.model.Prestamo;
import com.microlib.repository.PrestamoRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

/**
 * DataLoader de prestamo_ms.
 * Se ejecuta SOLO con el perfil "dev" activo y SOLO si la tabla está vacía.
 *
 * IMPORTANTE: requiere que libro_ms (puerto 8081) y usuario_ms (puerto 8082)
 * ya estén corriendo y tengan datos (sus propios DataLoaders), ya que aquí
 * se consultan vía Feign para obtener IDs válidos.
 *
 * Activar con: spring.profiles.active=dev
 */
@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private LibroClient libroClient;

    @Autowired
    private UsuarioClient usuarioClient;

    @Override
    public void run(String... args) throws Exception {

        if (prestamoRepository.count() > 0) {
            System.out.println("ℹ️ DataLoader (prestamo_ms): ya existen préstamos, no se insertan datos falsos.");
            return;
        }

        Faker faker = new Faker();
        Random random = new Random();
        String[] estados = {"ACTIVO", "DEVUELTO"};

        try {
            // Asumimos IDs 1..10 para libros y 1..5 para usuarios
            // (coinciden con los DataLoaders de libro_ms y usuario_ms)
            for (int i = 0; i < 8; i++) {
                Long libroId   = (long) faker.number().numberBetween(1, 11); // 1-10
                Long usuarioId = (long) faker.number().numberBetween(1, 6);  // 1-5

                LibroDTO libro     = libroClient.obtenerLibro(libroId);
                UsuarioDTO usuario = usuarioClient.obtenerUsuario(usuarioId);

                if (libro == null || usuario == null) continue;

                Prestamo prestamo = new Prestamo();
                prestamo.setLibroId(libro.getId());
                prestamo.setUsuarioId(usuario.getId());
                prestamo.setFechaPrestamo(LocalDate.now().minusDays(faker.number().numberBetween(0, 30)));
                prestamo.setEstado(estados[random.nextInt(estados.length)]);

                if ("DEVUELTO".equals(prestamo.getEstado())) {
                    prestamo.setFechaDevolucionReal(LocalDate.now());
                }

                prestamoRepository.save(prestamo);
            }

            System.out.println("✅ DataLoader (prestamo_ms): préstamos falsos insertados.");
        } catch (Exception e) {
            System.out.println("⚠️ DataLoader (prestamo_ms): no se pudieron crear préstamos. " +
                "¿Están corriendo libro_ms (8081) y usuario_ms (8082)? Detalle: " + e.getMessage());
        }
    }
}
