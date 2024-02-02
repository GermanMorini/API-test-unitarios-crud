package ger.morini.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Table(name = "products")
@Entity @Data
@AllArgsConstructor
@NoArgsConstructor
public class Producto {

      @Id
      private UUID id;

      @Column(length = 64, name = "product")
      private String nombre;

      @Column(name = "amount")
      private Integer cantidad;

      @Column(name = "expire-date")
      private LocalDate vencimiento;
}
