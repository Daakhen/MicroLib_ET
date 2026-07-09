package com.microlib;

import com.microlib.model.libro;
import com.microlib.repository.LibroRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * DataLoader de libro_ms.
 * Se ejecuta SOLO con el perfil "dev" activo y SOLO si la tabla está vacía
 * (para no duplicar datos cada vez que se reinicia la app).
 *
 * Activar con: spring.profiles.active=dev
 */
@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private LibroRepository libroRepository;

    @Override
    public void run(String... args) throws Exception {

        if (libroRepository.count() > 0) {
            System.out.println("ℹ️ DataLoader (libro_ms): ya existen libros, no se insertan datos falsos.");
            return;
        }

        Faker faker = new Faker();

        for (int i = 0; i < 10; i++) {
            libro lib = new libro();
            lib.setTitulo(faker.book().title());
            lib.setAutor(faker.book().author());
            lib.setIsbn(faker.number().digits(13));
            lib.setDisponible(faker.bool().bool());
            libroRepository.save(lib);
        }

        System.out.println("✅ DataLoader (libro_ms): 10 libros falsos insertados.");
    }
}
