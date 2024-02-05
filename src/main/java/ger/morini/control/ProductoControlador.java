package ger.morini.control;

import ger.morini.modelo.Producto;
import ger.morini.servicio.ProductoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Slf4j
public class ProductoControlador {

      @Autowired
      private ProductoService service;

      private byte[] recurso(String recurso) throws IOException {
            return FileCopyUtils.copyToByteArray(getClass().getResourceAsStream(recurso));
      }

      @GetMapping("/help")
      private ResponseEntity<byte[]> ayuda() throws IOException {
            log.debug("Mostrando página de inicio");

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(recurso("/templates/ayuda.html"));
      }

      @GetMapping("/retrieve")
      private ResponseEntity<List<Producto>> todos() {
            log.debug("Consultando todos los productos");

            return ResponseEntity.ok(service.findAll());
      }

      @GetMapping("/retrieve/byName")
      private ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
            log.debug("Consultando por nombre: %s".formatted(nombre));

            return service.findByNombre(nombre)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
      }

      @GetMapping("/retrieve/byId")
      private ResponseEntity<Producto> buscarPorId(@RequestParam UUID id) {
            log.debug("Consultando por id: %s".formatted(id));

            return service.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
      }

      @GetMapping("/expired")
      private ResponseEntity<List<Producto>> consultarVencidos() {
            log.debug("Consultando productos vencidos");

            return service.findExpired()
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
      }

      @PostMapping("/create")
      private ResponseEntity<Producto> guardar(@RequestBody Producto p) {
            log.debug("Guardando producto: %s".formatted(p));

            return service.create(p)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.badRequest().build());
      }

      @PutMapping("/update")
      private ResponseEntity<Producto> actualizar(@RequestBody Producto p) {
            log.debug("Actualizando producto: %s".formatted(p));

            return service.update(p)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.badRequest().build());
      }

      @DeleteMapping("/delete")
      private ResponseEntity<Producto> borrar(@RequestParam UUID id) {
            log.debug("Borrando el registro de ID: %s".formatted(id));

            return service.delete(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
      }
}
