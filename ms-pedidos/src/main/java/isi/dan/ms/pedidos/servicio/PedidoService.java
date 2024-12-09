package isi.dan.ms.pedidos.servicio;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import isi.dan.ms.pedidos.conf.RabbitMQConfig;
import isi.dan.ms.pedidos.dao.PedidoRepository;
import isi.dan.ms.pedidos.modelo.Cliente;
import isi.dan.ms.pedidos.modelo.DetallePedido;
import isi.dan.ms.pedidos.modelo.EstadoPedido;
import isi.dan.ms.pedidos.modelo.Pedido;
import isi.dan.ms.pedidos.modelo.Producto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    Logger log = LoggerFactory.getLogger(PedidoService.class);

    public Pedido savePedido(Pedido pedido) {
		// El id se pone automatico por mongo
        
        //f.1) Creacion de un pedido (asignacion de valores)
		pedido.setFecha(Instant.now());
        pedido.setNumeroPedido(this.getMaxNumeroPedido() + 1);
		BigDecimal totalDetalles = BigDecimal.ZERO;
        BigDecimal saldo = BigDecimal.ZERO;
		for (DetallePedido dp : pedido.getDetalle()) {
			totalDetalles.add(dp.getPrecioFinal());
		}
		pedido.setTotal(totalDetalles);


		RestTemplate restTemplate = new RestTemplate();
		String gatewayURL = "http://ms-gateway-svc:8080";
		
		Cliente clienteResult = restTemplate.getForObject(gatewayURL + "/clientes/api/clientes/" + pedido.getCliente().getId(), Cliente.class);
		pedido.setCliente(clienteResult);
		
        List<Pedido> pedidos = this.getAllPedidoByCliente(pedido.getCliente().getId().toString());
		

       //f.2) Creacion de un pedido (calculo del saldo del cliente)
		for (Pedido p : pedidos) {
			if (p.getEstado() == EstadoPedido.ACEPTADO || p.getEstado() == EstadoPedido.EN_PREPARACION)
				saldo = saldo.add(p.getTotal());
		}
		saldo = saldo.add(pedido.getTotal());

		if (saldo.compareTo(clienteResult.getMaximoDescubierto()) > 0) {
			pedido.updateState(EstadoPedido.RECHAZADO, pedido.getUsuario(), null);
			return pedidoRepository.save(pedido);
		} else {
			pedido.updateState(EstadoPedido.ACEPTADO, pedido.getUsuario(), null);
		}


		
        Boolean flagStockDisponible = true;
		for (DetallePedido dp : pedido.getDetalle()) {
			Producto productoResult = restTemplate.getForObject(gatewayURL + "/productos/api/productos/" + dp.getProducto().getId(), Producto.class);
			if (productoResult.getStockActual() < dp.getCantidad()) {
				log.info(dp.getProducto().getNombre() + " stock insuficiente");
				flagStockDisponible = false;
				break;
			} else {
				log.info(dp.getProducto().getNombre() + " hay stock ");
			}
		}

		//f.3) Creacion de un pedido (actualizacion de stock)
		if (flagStockDisponible) {
			pedido.updateState(EstadoPedido.EN_PREPARACION, pedido.getUsuario(), null);
			log.info("enviando update de stock de productos");
			for (DetallePedido dp : pedido.getDetalle()) {
				log.info("Enviando {}", dp.getProducto().getId() + ";" + dp.getCantidad());
				rabbitTemplate.convertAndSend(RabbitMQConfig.STOCK_UPDATE_QUEUE, dp.getProducto().getId() + ";" + dp.getCantidad());
			}

		}

		return pedidoRepository.save(pedido);
	}

    public void restockProducts(Pedido pedido) {
		log.info("enviando reestock de productos por pedido cancelado");
		for (DetallePedido dp : pedido.getDetalle()) {
			log.info("Enviando {}", dp.getProducto().getId() + ";" + dp.getCantidad());
			rabbitTemplate.convertAndSend(RabbitMQConfig.STOCK_UPDATE_QUEUE, dp.getProducto().getId() + ";" + (-dp.getCantidad()));
		}
	}

    public List<Pedido> getAllPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido getPedidoById(String id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    public void deletePedidoByNumero(String id) {
		pedidoRepository.deleteById(this.getPedidoByNroPedido(id).getId());
	}

	public Pedido update(Pedido pedidoUpdateable, String id) {
		pedidoUpdateable.setId(id);
		return pedidoRepository.save(pedidoUpdateable);
	}

    public List<Pedido> getAllPedidoByCliente(String id) {
		List<Pedido> pedidos = new ArrayList<Pedido>();
		log.info("Entré al servicio");
		for (Pedido p : this.getAllPedidos()) {
			log.info("pedido con id cliente:" + p.getCliente().getId());
			log.info("id argumento: " + id);
			if (String.valueOf(p.getCliente().getId()).equals(id)) {
				pedidos.add(p);
				log.info("Paso!");
			}
		}
		return pedidos;
	}

    public void deletePedido(String id) {
        pedidoRepository.deleteById(id);
    }

    public Pedido getPedidoByNroPedido(String nroPedido) {
		log.info("entrado a getPedidoByNro");
		Pedido pedido = new Pedido();
		for (Pedido p : this.getAllPedidos()) {

			if (p.getNumeroPedido() == Integer.parseInt(nroPedido)) {
				pedido = p;
				break;
			}
		}
		log.info("saliendo de getPedidoByNro");
		if (pedido.getId() == null)
			return null;
		return pedido;
	}

    public Integer getMaxNumeroPedido() {
		List<Pedido> pedidos = this.getAllPedidos();
		return pedidos.stream().map(Pedido::getNumeroPedido).max(Integer::compareTo).orElse(0);
	}
}
