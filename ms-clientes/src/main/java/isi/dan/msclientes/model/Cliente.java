package isi.dan.msclientes.model;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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


    @Column(name="MAXIMO_DESCUBIERTO")
    @Min(value = 0, message = "El descubierto maximo debe ser mayor a 0")
    private BigDecimal maximoDescubierto;

    @Column(name="MAXIMO_CANT_OBRAS_EJEC")
    @Min(value = 0, message = "El maximo de obras en ejecucion debe ser mayor a 0")
    private Integer maximoObrasEjecucionInteger;

    @OneToMany(mappedBy="cliente")
    private Set<Obra> obras;

    @OneToMany(mappedBy="cliente")
    private Set<Usuario> usuarios;

    @Value("${cliente.maximoDescubierto.default}")
    private transient BigDecimal maximoDescubiertoDefault;

    @PrePersist
    protected void onCreate() {
        if (maximoDescubierto == null) {
            maximoDescubierto = maximoDescubiertoDefault;
        }
    }
    
    // Métodos convenientes para agregar y remover usuarios
    public void addUsuario(Usuario usuario) {
        usuarios.add(usuario);
        usuario.setCliente(this);
    }
    
    public void removeUsuario(Usuario usuario) {
        usuarios.remove(usuario);
        usuario.setCliente(null);
    }

    // Métodos convenientes para agregar y remover obras
    public void addObra(Obra obra) {
        obras.add(obra);
        obra.setCliente(this);
    }
    
    public void removeObra(Obra obra) {
        obras.remove(obra);
        obra.setCliente(null);
    }
    
}
