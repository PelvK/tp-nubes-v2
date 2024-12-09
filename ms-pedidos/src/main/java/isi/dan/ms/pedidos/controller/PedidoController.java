package isi.dan.ms.pedidos.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import isi.dan.ms.pedidos.exception.IlegalStateException;
import isi.dan.ms.pedidos.exception.PedidoNotFoundException;
import isi.dan.ms.pedidos.modelo.EstadoPedido;
import isi.dan.ms.pedidos.modelo.Pedido;
import isi.dan.ms.pedidos.servicio.PedidoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    
    @Autowired
    private PedidoService pedidoService;

    Logger log = LoggerFactory.getLogger(PedidoController.class);

    @PostMapping
    public ResponseEntity<Pedido> createPedido(@RequestBody Pedido pedido) {
        Pedido savedPedido = pedidoService.savePedido(pedido);
        log.debug("Pedido" + pedido.getId() + "creado");
        return ResponseEntity.ok(savedPedido);
    }

    @GetMapping
    public List<Pedido> getAllPedidos() {
        return pedidoService.getAllPedidos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable String id) {
        Pedido pedido = pedidoService.getPedidoById(id);
        return pedido != null ? ResponseEntity.ok(pedido) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
	public ResponseEntity<Pedido> updateById(@PathVariable String id, @RequestBody Pedido pedidoUpdatear) throws PedidoNotFoundException {
		Pedido pedido = pedidoService.getPedidoById(id);
		if (pedido != null) {
			log.debug("Pedido actualizado!!");
			return ResponseEntity.ok(pedidoService.update(pedidoUpdatear, id));
		} else {
			throw new PedidoNotFoundException("Pedido '" + id + "' no encontrado");
		}
	}

    @PutMapping("/nroPedido/{nro}")
	public ResponseEntity<Pedido> updateByNroPedido(@PathVariable String nro, @RequestBody Pedido pedidoUpdatear)
			throws PedidoNotFoundException {
		Pedido pedido = pedidoService.getPedidoByNroPedido(nro);
		if (pedido != null) {
			log.debug("Pedido actualizado!!");
			return ResponseEntity.ok(pedidoService.update(pedidoUpdatear, pedido.getId()));
		} else {
			throw new PedidoNotFoundException("Pedido '" + nro + "' no encontrado");
		}
	}

    @GetMapping("/nroPedido/{id}")
	public ResponseEntity<Pedido> getPedidoBynro(@PathVariable String id) throws PedidoNotFoundException {
		log.info("Entrando al método getPedidoByNroPedido con id: " + id);

		Pedido pedido = pedidoService.getPedidoByNroPedido(id);
		if (pedido != null) {
			return ResponseEntity.ok(pedido);
		} else {
			throw new PedidoNotFoundException("Pedido '" + id + "' no encontrado");
		}
	}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable String id) {
        pedidoService.deletePedido(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/estado/{estado}")
	public ResponseEntity<Pedido> updateStateById(@PathVariable String id, @PathVariable String estado)
			throws PedidoNotFoundException, IlegalStateException {
		Pedido pedido = pedidoService.getPedidoById(id);
		if (pedido != null) {

			try {
				EstadoPedido estadoPedido = EstadoPedido.valueOf(estado.toUpperCase());
				if (estadoPedido == EstadoPedido.CANCELADO) {
                    //hay que handlear en caso de que se cancele la devolucion del stock
					pedidoService.restockProducts(pedido);
				}
				pedido.updateState(estadoPedido, pedido.getUsuario(), null);
				log.debug("Pedido actualizado!!");
				return ResponseEntity.ok(pedidoService.update(pedido, id));
			} catch (IllegalArgumentException ex) {
				throw new IlegalStateException("estado: " + estado + " no es un estado valido");
			}
		} else {
			throw new PedidoNotFoundException("Pedido '" + id + "' no encontrado");
		}

	}

	@PutMapping("/nroPedido/{nro}/estado/{estado}")
	public ResponseEntity<Pedido> updateStateByNroPedido(@PathVariable String nro, @PathVariable String estado)
			throws PedidoNotFoundException, IlegalStateException {
		Pedido pedido = pedidoService.getPedidoByNroPedido(nro);
		if (pedido != null) {

			try {
				EstadoPedido estadoPedido = EstadoPedido.valueOf(estado.toUpperCase());
				if (estadoPedido == EstadoPedido.CANCELADO) {
                    //hay que handlear en caso de que se cancele la devolucion del stock
					pedidoService.restockProducts(pedido);
				}
				pedido.updateState(estadoPedido, pedido.getUsuario(), null);
				log.debug("Pedido actualizado!!");
				return ResponseEntity.ok(pedidoService.update(pedido, pedido.getId()));
			} catch (IllegalArgumentException ex) {
				throw new IlegalStateException("estado: " + estado + " no es un estado valido");
			}
		} else {
			throw new PedidoNotFoundException("Pedido '" + nro + "' no encontrado");
		}

	}
}

