package isi.dan.ms.pedidos.modelo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import isi.dan.ms.pedidos.servicio.PedidoService;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "pedidos")
@Data
@NoArgsConstructor
public class Pedido {
	@Id
	String id;
	Instant fecha;
	Integer numeroPedido;
	String usuario;
	String observaciones;

	EstadoPedido estado;
	List<HistorialEstado> historialEstado = new ArrayList<HistorialEstado>();
	Obra obra;
	Cliente cliente;
	BigDecimal total;

	@Field("detalle")
	private List<DetallePedido> detalle;

	public void updateState(EstadoPedido nuevoEstado, String userEstado, String detalle) {
		Logger log = LoggerFactory.getLogger(PedidoService.class);

		historialEstado.add(new HistorialEstado(nuevoEstado, userEstado, detalle));
		this.estado = nuevoEstado;
		log.info("me he actualizado a: " + this.estado.toString());
	}

}

