package isi.dan.ms_productos.servicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isi.dan.ms_productos.conf.RabbitMQConfig;
import isi.dan.ms_productos.dao.ProductoRepository;
import isi.dan.ms_productos.dto.StockUpdateDTO;
import isi.dan.ms_productos.exception.ProductoNotFoundException;
import isi.dan.ms_productos.modelo.Producto;

import java.util.List;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;
    Logger log = LoggerFactory.getLogger(ProductoService.class);

    @RabbitListener(queues = RabbitMQConfig.STOCK_UPDATE_QUEUE)
    public void handleStockUpdate(Message msg) {
        log.info("Recibido {}", msg);
        // buscar el producto
        // actualizar el stock
        // verificar el punto de pedido y generar un pedido
    }



    public Producto saveProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    public Producto getProductoById(Long id) throws ProductoNotFoundException{
        return productoRepository.findById(id).orElseThrow(() -> new ProductoNotFoundException(id));
    }

    public void deleteProducto(Long id) {
        productoRepository.deleteById(id);
    }
}







package isi.dan.ms_productos.servicio;

import isi.dan.ms_productos.conf.RabbitMQConfig;
import isi.dan.ms_productos.dao.CategoriaRepository;
import isi.dan.ms_productos.dao.ProductoRepository;
import isi.dan.ms_productos.dto.StockUpdateDTO;
import isi.dan.ms_productos.exception.ProductoNotFoundException;
import isi.dan.ms_productos.modelo.Categoria;
import isi.dan.ms_productos.modelo.Producto;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {
	@Autowired
	private ProductoRepository productoRepository;
	@Autowired
	private CategoriaRepository categoriaRepository;

	Logger log = LoggerFactory.getLogger(ProductoService.class);

	@RabbitListener(queues = RabbitMQConfig.STOCK_UPDATE_QUEUE)
	public void handleStockUpdate(Message msg) throws ProductoNotFoundException {
		log.info("Recibido {}", msg);
		String body = new String(msg.getBody());
		Long productId = Long.parseLong(body.split(";")[0]);
		int stockChange = Integer.parseInt(body.split(";")[1]);
		Producto prod = productoRepository.findById(productId)
				.orElseThrow(() -> new ProductoNotFoundException(productId));
		prod.setStockActual(prod.getStockActual() - stockChange);
		if (prod.getStockActual() < prod.getStockMinimo()) {
			// generar pedido
			prod.setStockActual(prod.getStockActual() + 25);
		}
		this.saveProducto(prod);
	}

	public Producto saveProducto(Producto producto) {
		return productoRepository.save(producto);
	}

	public List<Producto> getAllProductos() {
		return productoRepository.findAll();
	}

	public Producto getProductoById(Long id) throws ProductoNotFoundException {
		return productoRepository.findById(id).orElseThrow(() -> new ProductoNotFoundException(id));
	}

	public void deleteProducto(Long id) {
		productoRepository.deleteById(id);
	}

	public List<Categoria> setCategorias(List<Categoria> categorias) {
		categoriaRepository.deleteAll();
		return categoriaRepository.saveAll(categorias);
	}

	public List<Categoria> getAllCategorias() {
		return categoriaRepository.findAll();
	}

	public Producto procesarOrdenProvision(StockUpdateDTO ordenProvision) throws ProductoNotFoundException {
		Producto prod = productoRepository.findById(ordenProvision.getIdProducto())
				.orElseThrow(() -> new ProductoNotFoundException(ordenProvision.getIdProducto()));
		prod.setStockActual(ordenProvision.getCantidad());
		prod.setPrecio(BigDecimal.valueOf(ordenProvision.getPrecio()));
		return this.saveProducto(prod);
	}

	public Producto actualizarDescuentoPromocional(Long id, Double descuentoPromocional)
			throws ProductoNotFoundException {
		Producto prod = productoRepository.findById(id).orElseThrow(() -> new ProductoNotFoundException(id));
		prod.setDescuentoPromocional(descuentoPromocional);
		return this.saveProducto(prod);
	}
}

