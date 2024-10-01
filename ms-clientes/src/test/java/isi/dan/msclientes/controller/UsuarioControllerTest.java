package isi.dan.msclientes.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import isi.dan.msclientes.model.Usuario;
import isi.dan.msclientes.servicios.ClienteService;
import isi.dan.msclientes.servicios.UsuarioService;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UsuarioService usuarioService;

	@MockBean
	private ClienteService clienteService;
	
	private Usuario usuario;
	
	@BeforeEach
	void setUp() {
		usuario = new Usuario();
		usuario.setId(1);
		usuario.setNombre("Usuario nombre");
		usuario.setApellido("Usuario apellido");
		usuario.setCorreoElectronico("test@usuario.com");
		//List<Cliente> clientes;
	}

	@Test
	void testGetAll() throws Exception {
		Mockito.when(usuarioService.findAll()).thenReturn(Collections.singletonList(usuario));

		mockMvc.perform(get("/api/usuarios"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].nombre").value("Usuario nombre"))
				.andExpect(jsonPath("$[0].apellido").value("Usuario apellido"))
				.andExpect(jsonPath("$[0].correoElectronico").value("test@usuario.com"));
	}

	@Test
	void testGetById() throws Exception {
		Mockito.when(usuarioService.findById(1)).thenReturn(Optional.of(usuario));

		mockMvc.perform(get("/api/usuarios/1"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.nombre").value("Usuario nombre"))
			.andExpect(jsonPath("$.apellido").value("Usuario apellido"))
			.andExpect(jsonPath("$.correoElectronico").value("test@usuario.com"));
	}
	
	@Test
	void testGetById_NotFound() throws Exception {
		Mockito.when(usuarioService.findById(2)).thenReturn(Optional.empty());

		mockMvc.perform(get("/api/usuarios/2")).andExpect(status().isNotFound());
	}
	
	@Test
	void testCreate() throws Exception {
		Mockito.when(usuarioService.save(Mockito.any(Usuario.class))).thenReturn(usuario);

		mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(asJsonString(usuario)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("Usuario nombre"))
				.andExpect(jsonPath("$.apellido").value("Usuario apellido"))
				.andExpect(jsonPath("$.correoElectronico").value("test@usuario.com"));
	}

	@Test
	void testUpdate() throws Exception {
		Mockito.when(usuarioService.findById(1)).thenReturn(Optional.of(usuario));
		Usuario usuarioUpdated = new Usuario();
		usuarioUpdated.setNombre("Test usuario updated");
		usuarioUpdated.setApellido("Usuario apellido");
		usuarioUpdated.setCorreoElectronico("test@usuario-updated.com");
		usuarioUpdated.setDni("43447175");

		Mockito.when(usuarioService.update(Mockito.any(Usuario.class))).thenReturn(usuarioUpdated);
		
		mockMvc.perform(
				put("/api/usuarios/1").contentType(MediaType.APPLICATION_JSON).content(asJsonString(usuarioUpdated)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("Test usuario updated"))
				.andExpect(jsonPath("$.apellido").value("Usuario apellido"))
				.andExpect(jsonPath("$.correoElectronico").value("test@usuario-updated.com"))
				.andExpect(jsonPath("$.dni").value("43447175"));
	}

	@Test
	void testDelete() throws Exception {
		Mockito.when(usuarioService.findById(1)).thenReturn(Optional.of(usuario));
		Mockito.doNothing().when(usuarioService).deleteById(1);

		mockMvc.perform(delete("/api/usuarios/1")).andExpect(status().isNoContent());
	}
	
	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
