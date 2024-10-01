package isi.dan.ms_productos.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "MS_PRD_CATEGORIA")
@Data
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    private String nombre;

    // Lo cambiamos para que sea dinamico desde la BD, los valores que puede tomar son:
    // CEMENTOS,PLACAS,PERFILES,MORTEROS,YESERIA
}
