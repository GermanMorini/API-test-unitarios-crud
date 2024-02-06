package ger.morini.repositorio;

import ger.morini.modelo.Producto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

// indica que es una prueba unitaria de la capa de persistencia (entidades y repsitorios)
// Configura al aplicación para usar la BDD H2
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // Esta en caso de usar otra BDD que no sea H2
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class) // le indicaremos el orden en que se ejecutarán las pruebas
//@Rollback(value = false) // evita que se eliminen los datos persistidos en cada prueba
public class RepositoryTest {

      @Autowired
      private ProductoRepositorio repo;

      private UUID uuid;
      private Producto test;

      @BeforeEach
      void setup() {
            uuid = UUID.randomUUID();
            test = new Producto(
                    uuid,
                    "Producto de prueba",
                    1234,
                    LocalDate.now().plusYears(2));
      }

      @Test
      @DisplayName("Creación de un producto")
      @Order(1) // indica el órden ...
      void crear() {
            Producto guardado = repo.save(test);

            assertNotNull(guardado);
      }

      @Test
      @DisplayName("Buscar producto por su nombre")
      @Order(2)
      void buscarPorNombre() {
            repo.save(test);

            assertFalse(repo.findByNombreContainsIgnoreCase("Producto ").get().isEmpty());
            assertFalse(repo.findByNombreContainsIgnoreCase(" de ").get().isEmpty());
            assertFalse(repo.findByNombreContainsIgnoreCase("to de").get().isEmpty());
            assertFalse(repo.findByNombreContainsIgnoreCase("to de ").get().isEmpty());
            assertFalse(repo.findByNombreContainsIgnoreCase("to de pru").get().isEmpty());
            assertFalse(repo.findByNombreContainsIgnoreCase("prueba").get().isEmpty());
            assertFalse(repo.findByNombreContainsIgnoreCase("ba").get().isEmpty());

            assertFalse(repo.findByNombreContainsIgnoreCase("pRODUCTO").get().isEmpty());
            assertFalse(repo.findByNombreContainsIgnoreCase(" De ").get().isEmpty());
            assertFalse(repo.findByNombreContainsIgnoreCase(" PrUeBa").get().isEmpty());
            assertFalse(repo.findByNombreContainsIgnoreCase(" dE PrueBA").get().isEmpty());

            assertTrue(repo.findByNombreContainsIgnoreCase("qrt").get().isEmpty());
            assertTrue(repo.findByNombreContainsIgnoreCase("TES T").get().isEmpty());
            assertTrue(repo.findByNombreContainsIgnoreCase(" NOMBRE").get().isEmpty());
            assertTrue(repo.findByNombreContainsIgnoreCase("!dwa; ").get().isEmpty());
      }

      @Test
      @DisplayName("Buscar producto por su ID")
      @Order(3)
      void buscarPorId() {
            repo.save(test);

            assertTrue(repo.findById(uuid).isPresent());
      }

      @Test
      @DisplayName("Actualizar registro")
      @Order(4)
      void actualizar() {
            test.setNombre("ACTUALIZADO");
            test.setCantidad(1234);
            test.setVencimiento(test.getVencimiento().minusYears(1234));

            repo.save(test);

            assertEquals(test, repo.findById(uuid).get());
      }

      @Test
      @DisplayName("Eliminar producto")
      @Order(5)
      void eliminar() {
            repo.save(test);
            repo.deleteById(uuid);

            assertTrue(repo.findById(test.getId()).isEmpty());
      }
}
