package isi.dan.ms_productos.modelo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MS_PRD_PRODUCTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor 
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String nombre;
    private String descripcion;
    @Column(name ="STOCK_ACTUAL")
    private int stockActual;
    @NotNull
    @Column(name ="STOCK_MINIMO")
    private int stockMinimo;
    @NotNull
    private BigDecimal precio;
    private double descuentoPromocional;
    
    @NotNull
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    
}
