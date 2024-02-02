package ger.morini.repositorio;

import ger.morini.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, UUID> {
      @Query("SELECT p FROM Producto p WHERE p.vencimiento < CURRENT_DATE")
      Optional<List<Producto>> findExpired();

      @Query("SELECT COUNT(*) FROM Producto p")
      int getProductCount();

      @Query("SELECT p FROM Producto p WHERE p.nombre LIKE %:nom%")
      Optional<List<Producto>> findByNombre(String nom);
}
