package isi.dan.msclientes.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isi.dan.msclientes.dao.ClienteRepository;
import isi.dan.msclientes.dao.ObraRepository;
import isi.dan.msclientes.enums.EstadoObra;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.Obra;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ObraService {
    
    @Autowired
    private ObraRepository obraRepository;

    @Autowired
	private ClienteService clienteService;

    public List<Obra> findAll() {
        return obraRepository.findAll();
    }

    public Optional<Obra> findById(Integer id) {
        return obraRepository.findById(id);
    }

    public Obra save(Obra obra) throws ClienteNotFoundException {
        try {
			Cliente cliente = clienteService.findById(obra.getCliente().getId()).orElseThrow();
			obra.setCliente(cliente);
			cliente.tomarObra();
			obra.setEstado(EstadoObra.HABILITADA);
		} catch (NoSuchElementException e) {
			throw new ClienteNotFoundException("Cliente " + obra.getCliente().getId() + " no encontrado");
		} catch (ObraCambiarEstadoInvalidoException e) {
			obra.setEstado(EstadoObra.PENDIENTE);
		}
		return obraRepository.save(obra);
    }

    public Obra update(Obra obra) {
        return obraRepository.save(obra);
    }

    public void deleteById(Integer id) {
        obraRepository.deleteById(id);
    }

    public void asignarCliente(Integer clienteId, Integer obraId) throws ObraNotFoundException, ClienteNotFoundException {
        Obra obra;
		Cliente cliente;
		try {
			obra = this.findById(obraId).orElseThrow();
		}catch(NoSuchElementException e) {
			throw new ObraNotFoundException("Obra " + obraId + " no encontrada");
		} try {
			cliente = clienteService.findById(clienteId).orElseThrow();
		}catch(NoSuchElementException e) {
			throw new ClienteNotFoundException("Cliente " + clienteId + " no encontrado");
		} 
		obra.setCliente(cliente);
		this.update(obra);
    }


    public Obra finalizarObra(Obra obra) throws ObraCambiarEstadoInvalidoException {
		if (obra.getEstado().equals(EstadoObra.FINALIZADA))
			throw new ObraCambiarEstadoInvalidoException("La obra ya se encuentra finalizada");

		if (obra.getEstado().equals(EstadoObra.PENDIENTE))
			throw new ObraCambiarEstadoInvalidoException("La obra debe estar habilitada para ser finalizada");

		obra.getCliente().liberarObra();
		obra.setEstado(EstadoObra.FINALIZADA);
		List<Obra> obras = obtenerObrasPorEstado(obra.getCliente().getId(), EstadoObra.PENDIENTE);
        if (obras.size > 0) {
            habilitarObra(obras.min(Comparator.comparing(Obra::getFecha)))
        }
		return this.update(obra);
	}
    
    private List<Obra> obtenerObrasPorEstado(Integer idCliente, EstadoObra estado) {
		if (estado == null)
			return obraRepository.findByClienteId(idCliente);
		return obraRepository.findByClienteIdAndEstadoEquals(idCliente, estado);
	}

    
    public Obra suspenderObra(Obra obra) throws ObraNotStateChangedException {
        if (obra.getEstado().equals(EstadoObra.FINALIZADA))
			throw new ObraNotStateChangedException("No se puede deshabilitar una obra finalizada");
		if (obra.getEstado().equals(EstadoObra.PENDIENTE))
			throw new ObraNotStateChangedException("La obra ya se encuentra pendiente");
		obra.getCliente().liberarObra();
		obra.setEstado(EstadoObra.PENDIENTE);
		return this.update(obra);
    }

    public Obra habilitarObra(Obra obra) throws ObraNotStateChangedException {
        if (obra.getEstado().equals(EstadoObra.FINALIZADA))
			throw new ObraNotStateChangedException("No se puede habilitar una obra finalizada");
		if (obra.getEstado().equals(EstadoObra.HABILITADA))
			throw new ObraNotStateChangedException("La obra ya se encuentra habilitada");
		obra.getCliente().tomarObra();
		obra.setEstado(EstadoObra.HABILITADA);
		return this.update(obra);
    }
}

