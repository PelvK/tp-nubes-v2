package isi.dan.ms.pedidos.modelo;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "obras")
public class Obra {
	Integer id;
	String direccion;
	Boolean esRemodelacion;
	float lat;
	float lng;
	Cliente cliente;
	BigDecimal presupuesto;
	EstadoObra estado = EstadoObra.PENDIENTE;

}
