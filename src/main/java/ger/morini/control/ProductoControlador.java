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
import java.util.Optional;
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
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(repo.findAll());
      }

      @GetMapping("/retrieve/byName")
      private ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
            return ResponseEntity.ok(repo.findByNombre(nombre));
      }

      @GetMapping("/retrieve/byId")
      private ResponseEntity<Optional<Producto>> buscarPorId(@RequestParam UUID id) {
            return ResponseEntity.ok(repo.findById(id));
      }

      @GetMapping("/expired")
      private ResponseEntity<List<Producto>> consultarVencidos() {
            log.debug("Consultando productos vencidos");
            return ResponseEntity.ok(repo.findExpired());
      }

      @PostMapping("/create")
      private ResponseEntity<Producto> guardar(@RequestBody Producto p) {
            log.debug("Guardando producto: %s".formatted(p));

            if (p.getCantidad() < 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(p);
            if (p.getId() == null) p.setId(UUID.randomUUID());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(repo.save(p));
      }

      @PutMapping("/update")
      private ResponseEntity<Producto> actualizar(@RequestBody Producto p) {
            Optional<Producto> tmp = repo.findById(p.getId());

            if (tmp.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            log.debug("Actualizando producto: %s por %s".formatted(tmp, p));

            Producto old = tmp.get();
            old.setNombre(p.getNombre());
            old.setCantidad(p.getCantidad());
            old.setVencimiento(p.getVencimiento());

            return ResponseEntity.ok(repo.save(old));
      }

      @DeleteMapping("/delete")
      private ResponseEntity<Object> borrar(@RequestParam UUID id) {
            log.debug("Borrando el registro de ID: %s".formatted(id));

            if (repo.findById(id).isEmpty()) {
                  return ResponseEntity.status(404).build();
            }

            repo.deleteById(id);

            return ResponseEntity.ok().build();
      }
}
