package isi.dan.msclientes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.servicios.ClienteService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombre("Test Cliente");
        cliente.setCorreoElectronico("test@cliente.com");
        cliente.setCuit("12998887776");
        cliente.setMaximoDescubierto(BigDecimal.valueOf(100000));
    }

    @Test
    void testGetAll() throws Exception {
        Mockito.when(clienteService.findAll()).thenReturn(Collections.singletonList(cliente));

        mockMvc.perform(get("/api/clientes")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre").value("Test Cliente"))
                .andExpect(jsonPath("$[0].correoElectronico").value("test@cliente.com"))
				.andExpect(jsonPath("$[0].maximoDescubierto").value(100000));
    }

    @Test
    void testGetById() throws Exception {
        Mockito.when(clienteService.findById(1)).thenReturn(Optional.of(cliente));

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("Test Cliente"))
				.andExpect(jsonPath("$.correoElectronico").value("test@cliente.com"))
				.andExpect(jsonPath("$.maximoDescubierto").value(150000));
    }
    
    @Test
    void testGetById_NotFound() throws Exception {
        Mockito.when(clienteService.findById(2)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clientes/2"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testCreate() throws Exception {
        Mockito.when(clienteService.save(Mockito.any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Test Cliente"))
                .andExpect(jsonPath("$.correoElectronico").value("test@cliente.com"))
				.andExpect(jsonPath("$.maximoDescubierto").value(150000));
    }

    @Test
    void testUpdate() throws Exception {
        Mockito.when(clienteService.findById(1)).thenReturn(Optional.of(cliente));
		Cliente clienteUpdated = new Cliente();
		clienteUpdated.setNombre("Test Cliente updated");
		clienteUpdated.setCorreoElectronico("test@cliente-updated.com");
		clienteUpdated.setMaximoDescubierto(BigDecimal.valueOf(10000));

		Mockito.when(clienteService.update(Mockito.any(Cliente.class))).thenReturn(clienteUpdated);
		
		mockMvc.perform(
				put("/api/clientes/1").contentType(MediaType.APPLICATION_JSON).content(asJsonString(clienteUpdated)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Test Cliente updated"))
				.andExpect(jsonPath("$.correoElectronico").value("test@cliente-updated.com"))
				.andExpect(jsonPath("$.maximoDescubierto").value(10000));
    }

    @Test
    void testDelete() throws Exception {
        Mockito.when(clienteService.findById(1)).thenReturn(Optional.of(cliente));
        Mockito.doNothing().when(clienteService).deleteById(1);

        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}