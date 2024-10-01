package isi.dan.msclientes.controller;

import isi.dan.msclientes.aop.LogExecutionTime;
import isi.dan.msclientes.exception.ClienteNotFoundException;
import isi.dan.msclientes.exception.UsuarioNotFoundException;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.Usuario;
import isi.dan.msclientes.servicios.ClienteService;
import isi.dan.msclientes.servicios.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	Logger log = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private ClienteService clienteService;

	@GetMapping
	@LogExecutionTime
	public List<Usuario> getAll() {
		return usuarioService.findAll();
	}

	@GetMapping("/{id}")
	@LogExecutionTime
	public ResponseEntity<Usuario> getById(@PathVariable Integer id) throws UsuarioNotFoundException {
		Optional<Usuario> usuario = usuarioService.findById(id);
		return ResponseEntity
				.ok(usuario.orElseThrow(() -> new UsuarioNotFoundException("Usuario " + id + " no encontrado")));
	}

	@PostMapping
	public Usuario create(@RequestBody @Valid Usuario usuario) {
		return usuarioService.save(usuario);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Usuario> update(@PathVariable Integer id, @RequestBody @Valid Usuario usuario)
			throws UsuarioNotFoundException {
		if (!usuarioService.findById(id).isPresent()) {
			throw new UsuarioNotFoundException("Usuario " + id + " no encontrado");
		}
		usuario.setId(id);
		return ResponseEntity.ok(usuarioService.update(usuario));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) throws UsuarioNotFoundException {
		if (!usuarioService.findById(id).isPresent()) {
			throw new UsuarioNotFoundException("Usuario " + id + " no encontrado");
		}
		usuarioService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}/asociar")
	public ResponseEntity<Usuario> asociarClienteUsuario(@PathVariable Integer id, @RequestBody List<Cliente> clientes)
			throws UsuarioNotFoundException, ClienteNotFoundException{
		Optional<Usuario> us = usuarioService.findById(id); 
		if (!us.isPresent()) {
			throw new UsuarioNotFoundException("Usuario " + id + " no encontrado");
		}
		for(Cliente c: clientes) {
			clienteService.findById(c.getId()).orElseThrow(() -> new ClienteNotFoundException("Cliente " + id + " no encontrado"));
		}
		Usuario usuario = us.get();
		usuario.setClientes(clientes);
		return ResponseEntity.ok(usuarioService.save(usuario));
	}
}
