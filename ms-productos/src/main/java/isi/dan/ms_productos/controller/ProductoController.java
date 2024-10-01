package isi.dan.ms_productos.controller;

import isi.dan.ms_productos.aop.LogExecutionTime;
import isi.dan.ms_productos.dto.StockUpdateDTO;
import isi.dan.ms_productos.exception.ProductoNotFoundException;
import isi.dan.ms_productos.modelo.Categoria;
import isi.dan.ms_productos.modelo.Producto;
import isi.dan.ms_productos.servicio.EchoClientFeign;
import isi.dan.ms_productos.servicio.ProductoService;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@CrossOrigin
@RestController
@RequestMapping("/api/productos")
public class ProductoController {
	@Autowired
	private ProductoService productoService;

	Logger log = LoggerFactory.getLogger(ProductoController.class);

	@Autowired
	EchoClientFeign echoSvc;

	@PostMapping
	@LogExecutionTime
	public ResponseEntity<Producto> createProducto(@RequestBody @Validated Producto producto) {
		Producto savedProducto = productoService.saveProducto(producto);
		return ResponseEntity.ok(savedProducto);
	}

	@GetMapping("/test1")
	@LogExecutionTime
	public String getEcho() {
		String resultado = echoSvc.echo();
		log.info("Log en test 1{}", resultado);
		return resultado;
	}

	@GetMapping("/test2")
	@LogExecutionTime
	public String getEcho2() {
		RestTemplate restTemplate = new RestTemplate();
		String gatewayURL = "http://ms-gateway-svc:8080";
		String resultado = restTemplate.getForObject(gatewayURL + "/clientes/api/clientes/echo", String.class);
		log.info("Log en test 2 {}", resultado);
		return resultado;
	}

	@GetMapping
	@LogExecutionTime
	public List<Producto> getAllProductos() {
		return productoService.getAllProductos();
	}

	@GetMapping("/{id}")
	@LogExecutionTime
	public ResponseEntity<Producto> getProductoById(@PathVariable Long id) throws ProductoNotFoundException {
		return ResponseEntity.ok(productoService.getProductoById(id));
	}

	@DeleteMapping("/{id}")
	@LogExecutionTime
	public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
		productoService.deleteProducto(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/orden")
	@LogExecutionTime
	public ResponseEntity<Producto> recibirOrdenProvision(@RequestBody @Valid StockUpdateDTO ordenProvision)
			throws ProductoNotFoundException {
		Producto prod = productoService.procesarOrdenProvision(ordenProvision);
		return ResponseEntity.ok(prod);
	}

	@PutMapping("/{id}")
	@LogExecutionTime
	public ResponseEntity<Producto> actualizarDescuentoPromocional(@PathVariable Long id,
			@RequestParam(name = "descuento_promocional", required = true) Double descuentoPromocional)
			throws ProductoNotFoundException {
		return ResponseEntity.ok(productoService.actualizarDescuentoPromocional(id, descuentoPromocional));
	}

	@GetMapping("/categorias")
	@LogExecutionTime
	public List<Categoria> getAllCategorias() {
		return productoService.getAllCategorias();
	}

	@PostMapping("/categorias")
	@LogExecutionTime
	public ResponseEntity<List<Categoria>> setCategorias(@RequestBody @Validated List<Categoria> categorias) {
		List<Categoria> savedCategorias = productoService.setCategorias(categorias);
		return ResponseEntity.ok(savedCategorias);
	}
}