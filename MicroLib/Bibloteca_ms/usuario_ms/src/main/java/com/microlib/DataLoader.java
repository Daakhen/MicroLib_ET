package com.microlib;

import com.microlib.model.Usuario;
import com.microlib.repository.UsuarioRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * DataLoader de usuario_ms.
 * Se ejecuta SOLO con el perfil "dev" activo y SOLO si la tabla está vacía.
 *
 * Activar con: spring.profiles.active=dev
 */
@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {

        if (usuarioRepository.count() > 0) {
            System.out.println("ℹ️ DataLoader (usuario_ms): ya existen usuarios, no se insertan datos falsos.");
            return;
        }

        Faker faker = new Faker();

        for (int i = 0; i < 5; i++) {
            Usuario usuario = new Usuario();
            usuario.setNombre(faker.name().fullName());
            usuario.setCorreo(faker.internet().emailAddress());
            usuarioRepository.save(usuario);
        }

        System.out.println("✅ DataLoader (usuario_ms): 5 usuarios falsos insertados.");
    }
}
