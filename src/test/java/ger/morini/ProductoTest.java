package ger.morini;

import ger.morini.modelo.Producto;
import ger.morini.repositorio.ProductoRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

// indica que es una prueba unitaria de una entidad
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // Esta en caso de usar otra BDD que no sea H2
@Slf4j
public class ProductoTest {

      @Autowired
      private ProductoRepositorio repo;

      @Test
      @DisplayName("""
              Creación de un producto
              """)
      void crear() {
            Producto p = new Producto("Test: creación", 123, LocalDate.now().plusYears(3));
            Producto guardado = repo.save(p);

            log.debug("Guardando producto: " + guardado);
            assertNotNull(guardado);
      }

      @Test
      @DisplayName("""
              Buscar producto por su nombre
              """)
      void buscarPorNombre() {
            UUID uuid = UUID.randomUUID();
            Producto test = new Producto(uuid, "Test: busqueda por nombre", 123, LocalDate.now());

            log.debug("Producto: " + test);

            repo.save(test);

            assertFalse(repo.findByNombre("Test: ").isEmpty());
            assertFalse(repo.findByNombre(": bus").isEmpty());
            assertFalse(repo.findByNombre("bre").isEmpty());
            assertFalse(repo.findByNombre(" por ").isEmpty());
            assertFalse(repo.findByNombre(" busqueda po").isEmpty());
            assertFalse(repo.findByNombre("queda").isEmpty());
      }

      @Test
      @DisplayName("""
              Buscar producto por su ID
              """)
      void buscarPorId() {
            UUID uuid = UUID.randomUUID();
            Producto test = new Producto(uuid, "Test: busqueda por id", 123, LocalDate.now());

            log.debug("Producto: " + test);

            repo.save(test);

            assertTrue(repo.findById(uuid).isPresent());
      }
}
