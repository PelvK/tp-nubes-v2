package isi.dan.ms.pedidos.controller;
import com.fasterxml.jackson.databind.ObjectMapper;

import isi.dan.ms.pedidos.controller.PedidoController;
import isi.dan.ms.pedidos.modelo.Cliente;
import isi.dan.ms.pedidos.modelo.DetallePedido;
import isi.dan.ms.pedidos.modelo.EstadoObra;
import isi.dan.ms.pedidos.modelo.EstadoPedido;
import isi.dan.ms.pedidos.modelo.Obra;
import isi.dan.ms.pedidos.modelo.Pedido;
import isi.dan.ms.pedidos.modelo.Producto;
import isi.dan.ms.pedidos.servicio.PedidoService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
public class PedidosControllerTests {

    @Autowired
	private MockMvc mockMvc;

	@MockBean
	private PedidoService PedidoService;

	private Cliente cliente;
    private Pedido pedido;
    private Pedido pedido2;
    private Obra obra;
    private Producto producto;
    @BeforeEach
	void setUp() {
        //cliente
        cliente = new Cliente();
        cliente.setId(101);
        cliente.setNombre("Juan Pérez");
        cliente.setCorreoElectronico("juan.perez@example.com");
        cliente.setCuit("20-12345678-9");
        cliente.setMaximoDescubierto(BigDecimal.valueOf(150000));
        //obra
        obra = new Obra();
        obra.setId(1);
        obra.setDireccion("Calle Falsa 123");
        obra.setEsRemodelacion(false);
        obra.setLat((float) -34.6037);
        obra.setLng((float) -58.3816);
        obra.setCliente(cliente);
        obra.setPresupuesto(new BigDecimal("500000.00"));
        obra.setEstado(EstadoObra.PENDIENTE);
        //producto
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto de prueba");
        producto.setDescripcion("Descripción del producto de prueba");
        producto.setPrecio(new BigDecimal("200.00"));
        producto.setStockActual(50);
        //detalle
        DetallePedido detallePedido = new DetallePedido();
        detallePedido.setProducto(producto);
        detallePedido.setCantidad(5);
        detallePedido.setPrecioUnitario(new BigDecimal("200.00"));
        detallePedido.setDescuento(new BigDecimal("10.00"));
        detallePedido.setPrecioFinal(new BigDecimal("990.00"));

        List<DetallePedido> detalles = new ArrayList<>();
        detalles.add(detallePedido);
		//pedido1
        pedido = new Pedido();
        pedido.setId("pedido123");
        //no la soporta
       // pedido.setFecha(Instant.now());
        pedido.setNumeroPedido(12345);
        pedido.setUsuario("usuario1");
        pedido.setObservaciones("Observaciones del pedido");
        pedido.setEstado(EstadoPedido.ACEPTADO);
       
        pedido.setObra(obra);
        pedido.setCliente(cliente);
        pedido.setTotal(new BigDecimal("1000.00"));
        pedido.setDetalle(detalles);
        //pedido2
        pedido2 = new Pedido();
        pedido2.setId("pedido123");
        //no la soporta
       // pedido.setFecha(Instant.now());
        pedido2.setNumeroPedido(12345);
        pedido2.setUsuario("usuario1");
        pedido2.setObservaciones("Observaciones del pedido");
        pedido2.setEstado(EstadoPedido.RECHAZADO);
       
        pedido2.setObra(obra);
        pedido2.setCliente(cliente);
        pedido2.setTotal(new BigDecimal("1000.00"));
        pedido2.setDetalle(detalles);
	}

    @Test
	void testGetAll() throws Exception {
		Mockito.when(PedidoService.getAllPedidos()).thenReturn(Collections.singletonList(pedido));

		mockMvc.perform(get("/api/pedidos")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].usuario").value("usuario1"))
				.andExpect(jsonPath("$[0].observaciones").value("Observaciones del pedido"))
				.andExpect(jsonPath("$[0].estado").value("ACEPTADO"));
	}


    @Test
	void testCreate() throws Exception {
		Mockito.when(PedidoService.savePedido(Mockito.any(Pedido.class))).thenReturn(pedido);

		mockMvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON).content(asJsonString(pedido)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.numeroPedido").value(12345))
				.andExpect(jsonPath("$.usuario").value("usuario1"))
                .andExpect(jsonPath("$.observaciones").value("Observaciones del pedido"))
				.andExpect(jsonPath("$.estado").value("ACEPTADO"));
		;
	}

    @Test
	void testDelete() throws Exception {
		Mockito.when(PedidoService.getPedidoByNroPedido("12345")).thenReturn(pedido);
		Mockito.doNothing().when(PedidoService).deletePedidoByNumero("12345");

		mockMvc.perform(delete("/api/pedidos/nroPedido/12345")).andExpect(status().isNoContent());
	}

    @Test
	void testUpdate() throws Exception {
		Mockito.when(PedidoService.getPedidoByNroPedido("12345")).thenReturn(pedido);
        Mockito.when(PedidoService.update(pedido, pedido.getId())).thenReturn(pedido2);
        //Mockito.when(PedidoService.update(Mockito.any(Pedido.class),EstadoPedido.RECHAZADO);
      mockMvc.perform(put("/api/pedidos/nroPedido/12345/estado/RECHAZADO")).andExpect(status().isOk())
      .andExpect(jsonPath("$.numeroPedido").value(12345))
      .andExpect(jsonPath("$.estado").value("RECHAZADO"));
		
	}

    private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
