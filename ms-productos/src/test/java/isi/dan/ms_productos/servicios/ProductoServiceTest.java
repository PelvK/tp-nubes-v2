package isi.dan.ms_productos.servicios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import isi.dan.ms_productos.dao.CategoriaRepository;
import isi.dan.ms_productos.dao.ProductoRepository;
import isi.dan.ms_productos.dto.StockUpdateDTO;
import isi.dan.ms_productos.exception.ProductoNotFoundException;
import isi.dan.ms_productos.modelo.Producto;
import isi.dan.ms_productos.servicio.ProductoService;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductoServiceTest {
	
	@Autowired
	private ProductoService productoService;
	
	@MockBean
	private ProductoRepository productoRepository;
	
	@MockBean
	private CategoriaRepository categoriaRepository;
	
	Producto prod;
	StockUpdateDTO orden;
	
	@BeforeEach
	void setUp() {
		prod = Producto.builder().build();
		orden = new StockUpdateDTO();
		orden.setIdProducto(1l);
		orden.setCantidad(5);
		orden.setPrecio(987.0);
    }
	@Test
	void procesarOrdenProvisionTest() throws ProductoNotFoundException {
		Mockito.when(productoRepository.findById(orden.getIdProducto())).thenReturn(Optional.of(prod));
		Mockito.when(productoRepository.save(Mockito.any(Producto.class))).thenAnswer(a -> a.getArguments()[0]);
		Producto prodUpdated = productoService.procesarOrdenProvision(orden);
		assertThat(prodUpdated.getStockActual()).isEqualTo(5);
		assertThat(prodUpdated.getPrecio()).isEqualTo(BigDecimal.valueOf(987.0));
	}
	
	@Test
	void procesarOrdenProvisionErrorTest() {
		Mockito.when(productoRepository.findById(orden.getIdProducto())).thenReturn(Optional.empty());
		assertThrows(ProductoNotFoundException.class, () -> productoService.procesarOrdenProvision(orden));
	}
	
	@Test
	void actualizarDescuentoPromocional() throws ProductoNotFoundException {
		Mockito.when(productoRepository.findById(orden.getIdProducto())).thenReturn(Optional.of(prod));
		Mockito.when(productoRepository.save(Mockito.any(Producto.class))).thenAnswer(a -> a.getArguments()[0]);
		Producto prodUpdated = productoService.actualizarDescuentoPromocional(orden.getIdProducto(), 555.0);
		assertThat(prodUpdated.getDescuentoPromocional()).isEqualTo(555.0);
	}
	
	@Test
	void actualizarDescuentoErrorPromocional() {
		Mockito.when(productoRepository.findById(orden.getIdProducto())).thenReturn(Optional.empty());
		assertThrows(ProductoNotFoundException.class, () -> productoService.actualizarDescuentoPromocional(orden.getIdProducto(), 555.0));
	}
}
