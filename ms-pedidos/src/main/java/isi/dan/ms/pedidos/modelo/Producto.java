package isi.dan.ms.pedidos.modelo;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "productos")
public class Producto {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int stockActual;
    
    
}
