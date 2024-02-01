package ger.morini.control;

import ger.morini.modelo.Producto;
import ger.morini.repositorio.ProductoRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
      private Object ayuda() throws IOException {
            log.debug("Mostrando página de inicio");
            return ResponseEntity
                    .status(200)
                    .contentType(MediaType.TEXT_HTML)
                    .body(recurso("/templates/ayuda.html"));
      }

      @GetMapping("/retrieve")
      private List<Producto> todos() {
            log.debug("Consultando todos los productos");
            return repo.findAll();
      }

      @GetMapping("/expired")
      private List<Producto> consultarVencidos() {
            log.debug("Consultando productos vencidos");
            return repo.findExpired();
      }

      @PostMapping("/create")
      private Producto guardar(@RequestBody Producto p) {
            log.debug("Guardando producto: %s".formatted(p));
            return repo.save(p);
      }

      @PutMapping("/update")
      private ResponseEntity<Object> actualizar(@RequestBody Producto p) {
            Optional<Producto> tmp = repo.findById(p.getId());

            if (tmp.isEmpty()) return ResponseEntity.status(404).build();

            log.debug("Actualizando producto: %s por %s".formatted(tmp, p));

            Producto old = tmp.get();
            old.setNombre(p.getNombre());
            old.setCantidad(p.getCantidad());
            old.setVencimiento(p.getVencimiento());

            repo.save(old);

            return ResponseEntity.ok().body(old);
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
