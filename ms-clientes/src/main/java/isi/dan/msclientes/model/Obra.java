package isi.dan.msclientes.model;

import java.math.BigDecimal;
import java.sql.Date;

import isi.dan.msclientes.enums.EstadoObra;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "MS_CLI_OBRA")
@Data
public class Obra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String direccion;

    @Column(name = "ES_REMODELACION")
    private Boolean esRemodelacion;
    
    private float lat;
    
    private float lng;
    
    @ManyToOne
    @JoinColumn(name = "ID_CLIENTE")
    private Cliente cliente;
    
    @NotNull(message = "El presupuesto es obligatorio")
    @Min(value=100, message = "El presupuesto debe ser al menos de 100") //VER DESPUES
    private BigDecimal presupuesto;

    @Enumerated(EnumType.STRING)
    private EstadoObra estado;

    @NotNull(message = "La fecha no puede ser nula")
    private Date fecha;

}
