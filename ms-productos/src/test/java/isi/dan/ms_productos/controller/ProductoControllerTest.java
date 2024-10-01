package isi.dan.ms_productos.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import isi.dan.ms_productos.dto.StockUpdateDTO;
import isi.dan.ms_productos.exception.ProductoNotFoundException;
import isi.dan.ms_productos.modelo.Producto;
import isi.dan.ms_productos.servicio.EchoClientFeign;
import isi.dan.ms_productos.servicio.ProductoService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductoService productoService;

	@MockBean
	private Logger log;

	@MockBean
	EchoClientFeign echoSvc;

	StockUpdateDTO orden;

	@BeforeEach
	void setUp() {
		orden = new StockUpdateDTO();
		orden.setIdProducto(1l);
		orden.setCantidad(55);
		orden.setPrecio(750.0);
	}

	@Test
	void recibirOrdenProvisionTest() throws Exception {
		Mockito.when(productoService.procesarOrdenProvision(orden))
				.thenReturn(Producto.builder().id(orden.getIdProducto()).stockActual(orden.getCantidad())
						.precio(BigDecimal.valueOf(orden.getPrecio())).build());
		mockMvc.perform(
				put("/api/productos/orden").contentType(MediaType.APPLICATION_JSON).content(asJsonString(orden)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1l))
				.andExpect(jsonPath("$.stockActual").value(55))
				.andExpect(jsonPath("$.precio").value(BigDecimal.valueOf(750.0)));
	}

	@Test
	void recibirOrdenProvisionErrorTest() throws Exception {
		Mockito.when(productoService.procesarOrdenProvision(orden)).thenThrow(ProductoNotFoundException.class);
		mockMvc.perform(
				put("/api/productos/orden").contentType(MediaType.APPLICATION_JSON).content(asJsonString(orden)))
				.andExpect(status().isNotFound());
	}

	@Test
	void actualizarDescuentoPromocionalTest() throws Exception {
		Long id = 1l;
		Double descuento = 333.3;

		Mockito.when(productoService.actualizarDescuentoPromocional(id, descuento))
				.thenReturn(Producto.builder().id(id).descuentoPromocional(descuento).build());
		mockMvc.perform(put("/api/productos/" + id + "?descuento_promocional=" + descuento)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id)).andExpect(jsonPath("$.descuentoPromocional").value(descuento));
	}

	@Test
	void actualizarDescuentoPromocionalErrorTest() throws Exception {
		Long id = 1l;
		Double descuento = 333.3;

		Mockito.when(productoService.actualizarDescuentoPromocional(id, descuento))
				.thenThrow(ProductoNotFoundException.class);
		mockMvc.perform(put("/api/productos/" + id + "?descuento_promocional=" + descuento)
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(orden))).andExpect(status().isNotFound());
	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}