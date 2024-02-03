package ger.morini.control;

import ger.morini.modelo.Producto;
import ger.morini.repositorio.ProductoRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
      private ProductoRepositorio repo;

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
            return ResponseEntity.ok(repo.findAll());
      }

      @GetMapping("/retrieve/byName")
      private ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
            return repo.findByNombreContainsIgnoreCase(nombre)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
      }

      @GetMapping("/retrieve/byId")
      private ResponseEntity<Producto> buscarPorId(@RequestParam UUID id) {
            return repo.findById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
      }

      @GetMapping("/expired")
      private ResponseEntity<List<Producto>> consultarVencidos() {
            log.debug("Consultando productos vencidos");
            return repo.findExpired()
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
      }

      @PostMapping("/create")
      private ResponseEntity<Producto> guardar(@RequestBody Producto p) {
            log.debug("Guardando producto: %s".formatted(p));

            if (p.getCantidad() < 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            if (p.getId() == null) p.setId(UUID.randomUUID());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(repo.save(p));
      }

      @PutMapping("/update")
      private ResponseEntity<Producto> actualizar(@RequestBody Producto p) {
            log.debug("Actualizando producto: %s".formatted(p));

            return repo.findById(p.getId()).map(pr -> {
                  pr.setNombre(p.getNombre());
                  pr.setCantidad(p.getCantidad());
                  pr.setVencimiento(p.getVencimiento());

                  return ResponseEntity.ok(repo.save(pr));
            }).orElseGet(() -> ResponseEntity.notFound().build());
      }

      @DeleteMapping("/delete")
      private ResponseEntity<Object> borrar(@RequestParam UUID id) {
            log.debug("Borrando el registro de ID: %s".formatted(id));

            if (repo.findById(id).isEmpty()) return ResponseEntity.status(404).build();

            repo.deleteById(id);

            return ResponseEntity.ok().build();
      }
}
