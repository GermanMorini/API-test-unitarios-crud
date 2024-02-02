package ger.morini;

import ger.morini.modelo.Producto;
import ger.morini.repositorio.ProductoRepositorio;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

// indica que es una prueba unitaria de una entidad
// Configura al aplicación para usar la BDD H2
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // Esta en caso de usar otra BDD que no sea H2
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class) // le indicaremos el orden en que se ejecutarán las pruebas
public class RepositoryTest {

      @Autowired
      private ProductoRepositorio repo;

      @Test
      @DisplayName("""
              Creación de un producto
              """)
      @Order(1) // indica el órden ...
      void crear() {
            Producto p = new Producto(UUID.randomUUID(), "Test: creación", 123, LocalDate.now().plusYears(3));
            Producto guardado = repo.save(p);
            assertNotNull(guardado);
      }

      @Test
      @DisplayName("""
              Buscar producto por su nombre
              """)
      @Order(2)
      @Rollback(value = false) // por defecto, cualquier cambio en la BDD se revierte con cada método de prueba, esto lo evita
      void buscarPorNombre() {
            UUID uuid = UUID.randomUUID();
            Producto test = new Producto(uuid, "Test: busqueda por nombre", 123, LocalDate.now());

            repo.save(test);

            assertFalse(repo.findByNombre("Test: ").get().isEmpty());
            assertFalse(repo.findByNombre(": bus").get().isEmpty());
            assertFalse(repo.findByNombre("bre").get().isEmpty());
            assertFalse(repo.findByNombre(" por ").get().isEmpty());
            assertFalse(repo.findByNombre(" busqueda po").get().isEmpty());
            assertFalse(repo.findByNombre("queda").get().isEmpty());

            assertTrue(repo.findByNombre("qrt").get().isEmpty());
            assertTrue(repo.findByNombre("TEST").get().isEmpty());
            assertTrue(repo.findByNombre("NOMBRE").get().isEmpty());
            assertTrue(repo.findByNombre("!dwa;").get().isEmpty());
      }

      @Test
      @DisplayName("""
              Buscar producto por su ID
              """)
      @Order(3)
      void buscarPorId() {
            UUID uuid = UUID.randomUUID();
            Producto test = new Producto(uuid, "Test: busqueda por id", 123, LocalDate.now());

            repo.save(test);

            assertTrue(repo.findById(uuid).isPresent());
      }

      @Test
      @DisplayName("""
              Actualizar registro
              """)
      @Order(4)
      void actualizar() {
            Producto p = repo.findAll().get(0);

            UUID id = p.getId();
            p.setNombre("NUEVO NOMBRE");
            p.setCantidad(3211);
            p.setVencimiento(p.getVencimiento().minusYears(1234));

            repo.save(p);

            assertEquals(p, repo.findById(id).get());
      }

      @Test
      @DisplayName("""
              Eliminar producto
              """)
      @Order(5)
      void eliminar() {
            Producto p = new Producto(UUID.randomUUID(), "Test de eliminación", 1234, LocalDate.now());

            repo.save(p);

            repo.deleteById(p.getId());

            assertTrue(repo.findById(p.getId()).isEmpty());
      }
}
