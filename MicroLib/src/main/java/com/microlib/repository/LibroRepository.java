package com.microlib.repository;

import com.microlib.model.libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface LibroRepository extends JpaRepository<libro, Long> {

}
