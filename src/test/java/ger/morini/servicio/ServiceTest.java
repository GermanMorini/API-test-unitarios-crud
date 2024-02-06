package ger.morini.servicio;

import ger.morini.modelo.Producto;
import ger.morini.repositorio.ProductoRepositorio;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTest {

      @Autowired
      private ProductoRepositorio repo;

      @Autowired
      private ProductoService service;

      private Producto valido, no_valido, expirado;

      @BeforeEach
      void instanciar() {
            valido = new Producto(UUID.randomUUID(), "Buen producto", 69, LocalDate.now().plusYears(1));
            no_valido = new Producto(UUID.randomUUID(), "Producto de mala calidad", -73, LocalDate.now());
            expirado = new Producto(UUID.randomUUID(), "Producto con mal olor", 420, LocalDate.now().minusYears(2));
      }

      @AfterEach
      void listaIgualABDD() {
            assert service.findAll().containsAll(repo.findAll()) : "Los datos del repositorio difieren a los de la lista";
            assert repo.findAll().containsAll(service.findAll()) : "Los datos de la lista difieren a los del repositorio";
            service.findAll().clear();
            repo.deleteAll();
      }

      @Test
      @DisplayName("Creación de un producto")
      @Order(1)
      void crear() {
            assertTrue(service.create(valido).isPresent());
            assertTrue(service.create(expirado).isPresent());
            assertTrue(service.create(no_valido).isEmpty());
      }

      @Test
      @DisplayName("Recuperar todos")
      @Order(2)
      void recuperarTodos() {
            service.create(valido);
            service.create(expirado);
            service.create(no_valido);

            List<Producto> guardados = service.findAll();

            assertTrue(guardados.contains(valido));
            assertTrue(guardados.contains(expirado));
            assertFalse(guardados.contains(no_valido));
      }

      @Test
      @DisplayName("Recuperar por nombre")
      @Order(3)
      void recuperarPorNombre() {
            service.create(valido);
            service.create(expirado);

            assertEquals(2, service.findByNombre("producto").get().size());
            assertFalse(service.findByNombre("uCt").get().isEmpty());
            assertFalse(service.findByNombre("bUEn ").get().isEmpty());
            assertFalse(service.findByNombre(" CoN MAL ").get().isEmpty());
            assertFalse(service.findByNombre(" OLOR").get().isEmpty());

            assertTrue(service.findByNombre("awda").get().isEmpty());
            assertTrue(service.findByNombre("AWAaawdA").get().isEmpty());
            assertTrue(service.findByNombre(":_12;").get().isEmpty());
      }

      @Test
      @DisplayName("Recuperar por id")
      @Order(4)
      void recuperarPorId() {
            service.create(valido);
            service.create(expirado);

            assertEquals(valido, service.findById(valido.getId()).get());
            assertEquals(expirado, service.findById(expirado.getId()).get());

            assertTrue(service.findById(UUID.randomUUID()).isEmpty());
      }

      @Test
      @DisplayName("Recuperar expirados")
      @Order(5)
      void recuperarExpirados() {
            service.create(expirado);

            List<Producto> expirados = service.findExpired().get();

            assertTrue(expirados.get(0).isVencido());
      }

      @Test
      @DisplayName("Actualizar un registro")
      @Order(6)
      void actualizar() {
            service.create(valido);
            service.create(expirado);

            Producto test1 = Producto.builder()
                    .id(valido.getId())
                    .nombre("Nombre cambiado!")
                    .cantidad(123)
                    .vencimiento(valido.getVencimiento().plusYears(123))
                    .build();

            Producto test2 = Producto.builder()
                    .id(expirado.getId())
                    .nombre("Otra instancia")
                    .cantidad(-4)
                    .vencimiento(expirado.getVencimiento())
                    .build();

            service.update(test1);
            service.update(test2);

            System.out.println(service.findById(test1.getId()));
            System.out.println(service.findById(test2.getId()));

            assertEquals(test1, service.findById(valido.getId()).get());
            assertNotEquals(test2, service.findById(expirado.getId()).get());
            assertTrue(service.findAll().contains(test1));
            assertNotEquals(test2.getCantidad(), service.findById(test2.getId()).get().getCantidad());
      }

      @Test
      @DisplayName("Eliminar un registro")
      @Order(7)
      void eliminar() {
            service.create(valido);
            service.create(expirado);
            service.create(no_valido);

            service.delete(valido.getId());
            service.delete(expirado.getId());
            service.delete(no_valido.getId());

            assertTrue(service.findById(valido.getId()).isEmpty());
            assertTrue(service.findById(expirado.getId()).isEmpty());
            assertTrue(service.findById(valido.getId()).isEmpty());
      }
}
