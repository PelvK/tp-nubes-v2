package isi.dan.msclientes.model;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "MS_CLI_CLIENTE")
@Data
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(name="CORREO_ELECTRONICO")
    @Email(regexp = "^[^@]+@[^@]+\\.com$", message = "Email debe ser valido")
    @NotBlank(message = "Email es obligatorio")
    private String correoElectronico;
    
    private String cuit;

    @OneToMany(mappedBy="cliente") //VER SI CON ESTO SE ROMPE
    private Set<Obra> obras;

    @OneToMany(mappedBy="cliente")
    private Set<Usuario> usuarios;

    @NotNull(message = "El maximo descubierto es obligatorio")
	@Column(name = "MAXIMO_DESCUBIERTO")
	@Min(value = 10000, message = "El descubierto maximo debe ser al menos 10000")
	BigDecimal maximoDescubierto;

	@Min(value = 0, message = "No se pueden tener cantidades negativas de obras disponibles a realizar")
	Integer cantObrasDisponibles;
	
	public void tomarObra() throws ObraCambiarEstadoInvalidoException {
		if(cantObrasDisponibles==0)
			throw new ObraCambiarEstadoInvalidoException("El cliente "+id+" ha superado su limite de obras habilitadas en simultaneo");
		cantObrasDisponibles--;
	}

	public void liberarObra() {
		cantObrasDisponibles++;
	}

    @Value("${cliente.maximoDescubierto.default}")
    private transient BigDecimal maximoDescubiertoDefault;

    @PrePersist
    protected void onCreate() {
        if (maximoDescubierto == null) {
            maximoDescubierto = maximoDescubiertoDefault;
        }
    }
}
