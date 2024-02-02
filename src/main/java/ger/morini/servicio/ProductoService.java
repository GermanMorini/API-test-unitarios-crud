package ger.morini.servicio;

import ger.morini.repositorio.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

      @Autowired
      private ProductoRepositorio repo;

}
