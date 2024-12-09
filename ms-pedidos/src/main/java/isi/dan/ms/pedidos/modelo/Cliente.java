package isi.dan.ms.pedidos.modelo;


import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "clientes")
public class Cliente {
    
    private Integer id;
    private String nombre;
    private String correoElectronico;
    private String cuit;
    private BigDecimal maximoDescubierto;

}
