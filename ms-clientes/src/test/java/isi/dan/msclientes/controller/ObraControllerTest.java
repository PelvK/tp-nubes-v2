package isi.dan.msclientes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import isi.dan.msclientes.model.Obra;
import isi.dan.msclientes.servicios.ObraService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ObraController.class)
public class ObraControllerTest {
@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ObraService obraService;

	private Obra obra;

	@BeforeEach
	void setUp() {
		obra = new Obra();
		obra.setId(1);
		obra.setDireccion("Direccion Test Obra");
		obra.setLat(-12.5f);
		obra.setLng(21.7f);
		obra.setPresupuesto(BigDecimal.valueOf(105));
		Cliente cliente = new Cliente();
		cliente.setId(1);
		obra.setCliente(cliente);
	}

	@Test
	void testGetAll() throws Exception {
		Mockito.when(obraService.findAll()).thenReturn(Collections.singletonList(obra));

		mockMvc.perform(get("/api/obras")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].direccion").value("Direccion Test Obra"))
				.andExpect(jsonPath("$[0].lat").value(-12.5f))
				.andExpect(jsonPath("$[0].lng").value(21.7f))
				.andExpect(jsonPath("$[0].presupuesto").value(105))
				.andExpect(jsonPath("$[0].cliente.id").value(1));
	}

	@Test
	void testGetById() throws Exception {
		Mockito.when(obraService.findById(1)).thenReturn(Optional.of(obra));

		mockMvc.perform(get("/api/obras/1")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.direccion").value("Direccion Test Obra"))
				.andExpect(jsonPath("$.lat").value(-12.5f))
				.andExpect(jsonPath("$.lng").value(21.7f))
				.andExpect(jsonPath("$.presupuesto").value(105))
				.andExpect(jsonPath("$.cliente.id").value(1));
	}

	@Test
	void testCreate() throws Exception {
		Mockito.when(obraService.save(Mockito.any(Obra.class))).thenReturn(obra);

		mockMvc.perform(post("/api/obras").contentType(MediaType.APPLICATION_JSON).content(asJsonString(obra)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.direccion").value("Direccion Test Obra"))
				.andExpect(jsonPath("$.lat").value(-12.5f))
				.andExpect(jsonPath("$.lng").value(21.7f))
				.andExpect(jsonPath("$.presupuesto").value(105))
				.andExpect(jsonPath("$.cliente.id").value(1));
	}

	@Test
	void testUpdate() throws Exception {
		Mockito.when(obraService.findById(1)).thenReturn(Optional.of(obra));
		
		Obra obraUpdated = new Obra();
		obraUpdated.setId(1);
		obraUpdated.setDireccion("Direccion Test Obra updated");
		obraUpdated.setLat(0);
		obraUpdated.setLng(0);
		obraUpdated.setPresupuesto(BigDecimal.valueOf(110));
		Cliente cliente = new Cliente();
		cliente.setId(1);
		obraUpdated.setCliente(cliente);
		
		Mockito.when(obraService.update(Mockito.any(Obra.class))).thenReturn(obraUpdated);
		
		mockMvc.perform(put("/api/obras/1").contentType(MediaType.APPLICATION_JSON).content(asJsonString(obra)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.direccion").value("Direccion Test Obra updated"))
			.andExpect(jsonPath("$.lat").value(0))
			.andExpect(jsonPath("$.lng").value(0))
			.andExpect(jsonPath("$.presupuesto").value(110))
			.andExpect(jsonPath("$.cliente.id").value(1));
	}

	@Test
	void testDelete() throws Exception {
		Mockito.when(obraService.findById(1)).thenReturn(Optional.of(obra));
		Mockito.doNothing().when(obraService).deleteById(1);

		mockMvc.perform(delete("/api/obras/1")).andExpect(status().isNoContent());
	}

	@Test
	void habilitarTest() throws Exception {
		Mockito.when(obraService.findById(1)).thenReturn(Optional.of(obra));
		Mockito.when(obraService.habilitar(obra)).thenReturn(obra);
		mockMvc.perform(put("/api/obras/1/habilitar").contentType(MediaType.APPLICATION_JSON).content(asJsonString(obra)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.direccion").value("Direccion Test Obra"))
			.andExpect(jsonPath("$.lat").value(-12.5f))
			.andExpect(jsonPath("$.lng").value(21.7f))
			.andExpect(jsonPath("$.presupuesto").value(105))
			.andExpect(jsonPath("$.cliente.id").value(1));
	}
	
	@Test
	void habilitarErrorTest() throws Exception {
		// not found
		Mockito.when(obraService.findById(1)).thenReturn(Optional.empty());
		mockMvc.perform(put("/api/obras/1/habilitar").contentType(MediaType.APPLICATION_JSON).content(asJsonString(obra)))
			.andExpect(status().isNotFound());
		// conflict
		Mockito.when(obraService.findById(1)).thenReturn(Optional.of(obra));
		Mockito.when(obraService.habilitar(obra)).thenThrow(ObraCambiarEstadoInvalidoException.class);
		mockMvc.perform(put("/api/obras/1/habilitar").contentType(MediaType.APPLICATION_JSON).content(asJsonString(obra)))
			.andExpect(status().isConflict());
	}
	
	@Test
	void deshabilitarTest() throws Exception {
		Mockito.when(obraService.findById(1)).thenReturn(Optional.of(obra));
		Mockito.when(obraService.deshabilitar(obra)).thenReturn(obra);
		mockMvc.perform(put("/api/obras/1/deshabilitar").contentType(MediaType.APPLICATION_JSON).content(asJsonString(obra)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.direccion").value("Direccion Test Obra"))
			.andExpect(jsonPath("$.lat").value(-12.5f))
			.andExpect(jsonPath("$.lng").value(21.7f))
			.andExpect(jsonPath("$.presupuesto").value(105))
			.andExpect(jsonPath("$.cliente.id").value(1));
	}
	
	@Test
	void deshabilitarErrorTest() throws Exception {
		// not found
		Mockito.when(obraService.findById(1)).thenReturn(Optional.empty());
		mockMvc.perform(put("/api/obras/1/deshabilitar").contentType(MediaType.APPLICATION_JSON).content(asJsonString(obra)))
			.andExpect(status().isNotFound());
		// conflict
		Mockito.when(obraService.findById(1)).thenReturn(Optional.of(obra));
		Mockito.when(obraService.deshabilitar(obra)).thenThrow(ObraCambiarEstadoInvalidoException.class);
		mockMvc.perform(put("/api/obras/1/deshabilitar").contentType(MediaType.APPLICATION_JSON).content(asJsonString(obra)))
			.andExpect(status().isConflict());
	}
	
	@Test
	void finalizarTest() throws Exception {
		Mockito.when(obraService.findById(1)).thenReturn(Optional.of(obra));
		Mockito.when(obraService.finalizar(obra)).thenReturn(obra);
		mockMvc.perform(put("/api/obras/1/finalizar").contentType(MediaType.APPLICATION_JSON).content(asJsonString(obra)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.direccion").value("Direccion Test Obra"))
			.andExpect(jsonPath("$.lat").value(-12.5f))
			.andExpect(jsonPath("$.lng").value(21.7f))
			.andExpect(jsonPath("$.presupuesto").value(105))
			.andExpect(jsonPath("$.cliente.id").value(1));
	}
	
	@Test
	void finalizarErrorTest() throws Exception {
		// not found
		Mockito.when(obraService.findById(1)).thenReturn(Optional.empty());
		mockMvc.perform(put("/api/obras/1/finalizar").contentType(MediaType.APPLICATION_JSON).content(asJsonString(obra)))
			.andExpect(status().isNotFound());
		// conflict
		Mockito.when(obraService.findById(1)).thenReturn(Optional.of(obra));
		Mockito.when(obraService.finalizar(obra)).thenThrow(ObraCambiarEstadoInvalidoException.class);
		mockMvc.perform(put("/api/obras/1/finalizar").contentType(MediaType.APPLICATION_JSON).content(asJsonString(obra)))
			.andExpect(status().isConflict());
	}	
	
	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}