package isi.dan.msclientes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import isi.dan.msclientes.aop.LogExecutionTime;
import isi.dan.msclientes.exception.ClienteNotFoundException;
import isi.dan.msclientes.exception.ObraNotFoundException;
import isi.dan.msclientes.exception.ObraNotStateChangedException;
import isi.dan.msclientes.model.Obra;
import isi.dan.msclientes.servicios.ObraService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/obras")
public class ObraController {

    @Autowired
    private ObraService obraService;

    @GetMapping
    @LogExecutionTime
    public List<Obra> getAll() throws ObraNotFoundException {
        return obraService.findAll();
    }

	@GetMapping("/{id}")
	@LogExecutionTime
	public ResponseEntity<Obra> getById(@PathVariable Integer id) throws ObraNotFoundException {
		Optional<Obra> obra = obraService.findById(id);
		return ResponseEntity.ok(obra.orElseThrow(() -> new ObraNotFoundException("Obra " + id + " no encontrada")));
	}

	@PostMapping
	public Obra create(@RequestBody @Valid Obra obra) throws ClienteNotFoundException {
		return obraService.save(obra);
	}

	@PutMapping("/{id}")
	//REVISAR ESTO @V
	public ResponseEntity<Obra> update(@PathVariable Integer id, @RequestBody Obra obra)
			throws ObraNotFoundException {
		if (!obraService.findById(id).isPresent()) {
			throw new ObraNotFoundException("Obra " + id + " no encontrada");
		}
		obra.setId(id);
		return ResponseEntity.ok(obraService.update(obra));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) throws ObraNotFoundException {
		if (!obraService.findById(id).isPresent()) {
			throw new ObraNotFoundException("Obra " + id + " no encontrada");
		}
		obraService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}/asignar/{idCliente}")
	public ResponseEntity<Void> asignarCliente(@PathVariable Integer id, @PathVariable Integer idCliente)
			throws ObraNotFoundException, ClienteNotFoundException {
		obraService.asignarCliente(id, idCliente);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}/habilitar")
	public ResponseEntity<Obra> habilitar(@PathVariable Integer id)
			throws ObraNotFoundException, ObraNotStateChangedException {
		Optional<Obra> obraOpt = obraService.findById(id);
		if (!obraOpt.isPresent()) {
			throw new ObraNotFoundException("Obra " + id + " no encontrada");
		}
		return ResponseEntity.ok(obraService.habilitarObra(obraOpt.get()));
	}

	@PutMapping("/{id}/deshabilitar")
	public ResponseEntity<Obra> deshabilitar(@PathVariable Integer id)
			throws ObraNotFoundException, ObraNotStateChangedException {
		Optional<Obra> obraOpt = obraService.findById(id);
		if (!obraOpt.isPresent()) {
			throw new ObraNotFoundException("Obra " + id + " no encontrada");
		}
		return ResponseEntity.ok(obraService.suspenderObra(obraOpt.get()));
	}

	@PutMapping("/{id}/finalizar")
	public ResponseEntity<Obra> finalizar(@PathVariable Integer id)
			throws ObraNotFoundException, ObraNotStateChangedException {
		Optional<Obra> obraOpt = obraService.findById(id);
		if (!obraOpt.isPresent()) {
			throw new ObraNotFoundException("Obra " + id + " no encontrada");
		}
		return ResponseEntity.ok(obraService.finalizarObra(obraOpt.get()));
	}
}
