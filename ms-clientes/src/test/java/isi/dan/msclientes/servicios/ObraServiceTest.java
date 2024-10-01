package isi.dan.msclientes.servicios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import isi.dan.msclientes.dao.ObraRepository;
import isi.dan.msclientes.exception.ClienteNotFoundException;
import isi.dan.msclientes.exception.ObraCambiarEstadoInvalidoException;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.EstadoObra;
import isi.dan.msclientes.model.Obra;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class ObraServiceTest {
	
	@Autowired
	private ObraService obraService;
	
	@MockBean
	private ObraRepository obraRepository;
	
	@MockBean
	private ClienteService clienteService;
	
	private Cliente clienteDisponible, clienteLleno, clienteNoEncontrado;
	
	private Obra obraNoCliente, obraDisponible, obraLlena;
	
	@BeforeEach
    void setUp() {
		clienteDisponible = new Cliente();
		clienteLleno = new Cliente();
		clienteNoEncontrado = new Cliente();
		
		clienteDisponible.setId(1);
		clienteDisponible.setCantObrasDisponibles(2);
		clienteLleno.setId(2);
		clienteLleno.setCantObrasDisponibles(0);
		clienteNoEncontrado.setId(3);
		
		obraNoCliente = new Obra();
		obraDisponible = new Obra();
		obraLlena = new Obra();
		
		obraNoCliente.setCliente(clienteNoEncontrado);
		obraNoCliente.setDireccion("Dir 123");
		
		obraDisponible.setCliente(clienteDisponible);
		obraDisponible.setDireccion("Dir 456");
		
		obraLlena.setCliente(clienteLleno);
		obraLlena.setDireccion("Dir 789");
		
    }
	
	@Test
	void saveClienteNotFoundTest() {
		Mockito.when(clienteService.findById(3)).thenReturn(Optional.<Cliente>empty());
		assertThrows(ClienteNotFoundException.class, () -> obraService.save(obraNoCliente));
	}
	
	@Test
	void saveObraPendienteTest() throws ClienteNotFoundException {
		Mockito.when(clienteService.findById(2)).thenReturn(Optional.of(clienteLleno));
		Mockito.when(obraRepository.save(obraLlena)).thenReturn(obraLlena);
		obraLlena = obraService.save(obraLlena);
		assertThat(obraLlena.getEstado()).isEqualTo(EstadoObra.PENDIENTE);
		assertThat(obraLlena.getDireccion()).isEqualTo("Dir 789");
		assertThat(obraLlena.getCliente().getCantObrasDisponibles()).isEqualTo(0);
	}
	
	@Test
	void saveObraHabilitadaTest() throws ClienteNotFoundException {
		Mockito.when(clienteService.findById(1)).thenReturn(Optional.of(clienteDisponible));
		Mockito.when(obraRepository.save(obraDisponible)).thenReturn(obraDisponible);
		obraDisponible = obraService.save(obraDisponible);
		assertThat(obraDisponible.getEstado()).isEqualTo(EstadoObra.HABILITADA);
		assertThat(obraDisponible.getDireccion()).isEqualTo("Dir 456");
		assertThat(obraDisponible.getCliente().getCantObrasDisponibles()).isEqualTo(1);
	}
	
	@Test
	void habilitarTest() throws ObraCambiarEstadoInvalidoException {
		Cliente cliente = new Cliente();
		cliente.setId(1);
		cliente.setCantObrasDisponibles(1);
		Obra obra = new Obra();
		obra.setEstado(EstadoObra.PENDIENTE);
		obra.setCliente(cliente);
		Obra obra2 = new Obra();
		obra2.setEstado(EstadoObra.PENDIENTE);
		obra2.setCliente(cliente);
		
		// No puedo deshabilitar una obra pendiente
		Mockito.when(obraRepository.save(obra)).thenReturn(obra);
		Mockito.when(obraRepository.save(obra2)).thenReturn(obra2);
		assertThrows(ObraCambiarEstadoInvalidoException.class, () -> obraService.deshabilitar(obra));
		assertThrows(ObraCambiarEstadoInvalidoException.class, () -> obraService.finalizar(obra));
		
		// Habilito obra
		Obra obraHabilitada = obraService.habilitar(obra);
		assertThat(obraHabilitada.getEstado()).isEqualTo(EstadoObra.HABILITADA);
		assertThat(obraHabilitada.getCliente().getCantObrasDisponibles()).isEqualTo(0);
		
		// Habilito obra2 error no hay disponible
		assertThrows(ObraCambiarEstadoInvalidoException.class, () -> obraService.habilitar(obra2));
		
		// Habilita una obra habilitada, error
		Mockito.when(obraRepository.save(obraHabilitada)).thenReturn(obraHabilitada);
		assertThrows(ObraCambiarEstadoInvalidoException.class, () -> obraService.habilitar(obraHabilitada));
		
		Mockito.when(obraRepository.findByClienteIdAndEstadoEquals(1, EstadoObra.PENDIENTE)).thenReturn(List.of(obra2));
		Obra obraFinalizada = obraService.finalizar(obraHabilitada);
		assertThat(obraHabilitada.getEstado()).isEqualTo(EstadoObra.FINALIZADA);
		assertThat(obraHabilitada.getCliente().getCantObrasDisponibles()).isEqualTo(0);
		assertThat(obra2.getEstado()).isEqualTo(EstadoObra.HABILITADA);

		Mockito.when(obraRepository.save(obraFinalizada)).thenReturn(obraFinalizada);
		assertThrows(ObraCambiarEstadoInvalidoException.class, () -> obraService.habilitar(obraFinalizada));
		assertThrows(ObraCambiarEstadoInvalidoException.class, () -> obraService.deshabilitar(obraFinalizada));
		assertThrows(ObraCambiarEstadoInvalidoException.class, () -> obraService.finalizar(obraFinalizada));
		
		Obra obraPendiente = obraService.deshabilitar(obra2);
		assertThat(obraPendiente.getEstado()).isEqualTo(EstadoObra.PENDIENTE);
		assertThat(obraPendiente.getCliente().getCantObrasDisponibles()).isEqualTo(1);
	}
}