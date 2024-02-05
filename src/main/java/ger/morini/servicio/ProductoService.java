package ger.morini.servicio;

import ger.morini.modelo.Producto;
import ger.morini.repositorio.ProductoRepositorio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductoService {

      @Autowired
      private ProductoRepositorio repo;

      private List<Producto> datos;

      // indica que se ejecutará luego de iniciar el bean
      @PostConstruct
      public void cargarEntidades() {
            datos = repo.findAll();
      }

      public List<Producto> findAll() {
            return datos;
      }

      public Optional<Producto> findById(UUID id) {
            return datos.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst();
      }

      public Optional<List<Producto>> findByNombre(String nombre) {
            return Optional.of(datos.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                    .toList());
      }

      public Optional<List<Producto>> findExpired() {
            return Optional.of(datos.stream()
                    .filter(Producto::isVencido)
                    .toList());
      }

      public Optional<Producto> create(Producto p) {
            if (p.getCantidad() < 0) return Optional.empty();
            if (p.getId() == null) p.setId(UUID.randomUUID());

            if (!datos.contains(repo.save(p))) datos.add(p);

            return Optional.of(p);
      }

      public Optional<Producto> update(Producto nuevo) {
            return findById(nuevo.getId()).map(old -> {
                  datos.remove(old);

                  old.setNombre(nuevo.getNombre());
                  old.setCantidad(nuevo.getCantidad());
                  old.setVencimiento(nuevo.getVencimiento());

                  datos.add(old);
                  return Optional.of(repo.save(old));
            }).orElse(Optional.empty());
      }

      public Optional<Producto> delete(UUID id) {
            return findById(id).map(pr -> {
                  repo.deleteById(id);
                  datos.remove(pr);
                  return Optional.of(pr);
            }).orElse(Optional.empty());
      }
}
