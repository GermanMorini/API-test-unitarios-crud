package ger.morini.servicio;

import ger.morini.modelo.Producto;
import ger.morini.repositorio.ProductoRepositorio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// TODO: terminar esto
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
}
