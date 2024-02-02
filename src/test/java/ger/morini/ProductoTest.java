package ger.morini;

import ger.morini.modelo.Producto;
import ger.morini.repositorio.ProductoRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// indica que es una prueba unitaria de una entidad
@DataJpaTest
//@AutoConfigureTestDatabase(replace = Replace.NONE) // Esta en caso de usar otra BDD que no sea H2
@Slf4j
public class ProductoTest {

      @Autowired
      private ProductoRepositorio repo;

      @Test
      void crear() {
            Producto p = new Producto("Test 1", 123, LocalDate.now().plusYears(3));
            Producto guardado = repo.save(p);

            log.debug("Guardando producto: " + guardado);
            assertNotNull(guardado);
      }
}
