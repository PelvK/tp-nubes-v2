package isi.dan.msclientes.servicios;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import isi.dan.msclientes.dao.ClienteRepository;
import isi.dan.msclientes.model.Cliente;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class ClienteServiceTest {
	
	@Autowired
	private ClienteService clienteService;
	
	@MockBean
	private ClienteRepository clienteRepository;
	
	private Cliente clienteUno, clienteDos;
	
	@BeforeEach
    void setUp() {
        clienteUno = new Cliente();
        clienteUno.setId(1);
        clienteUno.setNombre("cliente1");
        clienteUno.setCorreoElectronico("cliente1@mail.com");
        clienteUno.setMaximoDescubierto(BigDecimal.valueOf(15000));
        
        clienteDos = new Cliente();
        clienteDos.setId(2);
        clienteDos.setNombre("cliente2");
        clienteDos.setCorreoElectronico("cliente2@mail.com");
        clienteDos.setMaximoDescubierto(BigDecimal.valueOf(25000));
    }
	
	@Test
	void findAllTest() {
		List<Cliente> clientes = List.of(clienteUno, clienteDos);
		Mockito.when(clienteRepository.findAll()).thenReturn(clientes);
		assertEquals(clienteService.findAll(), clientes);
		verify(clienteRepository, times(1)).findAll();
	}
	
	@Test
	void findByIdTest() {
		Mockito.when(clienteRepository.findById(2)).thenReturn(Optional.of(clienteDos));
		Optional<Cliente> c2 = clienteService.findById(clienteDos.getId());
		assertThat(c2).isPresent();
		assertThat(c2.get().getNombre()).isEqualTo("cliente2");
		assertThat(c2.get().getCorreoElectronico()).isEqualTo("cliente2@mail.com");
		assertThat(c2.get().getMaximoDescubierto()).isEqualTo(BigDecimal.valueOf(25000));
	}
	
	@Test
	void saveAndUpdateTest() {
		Mockito.when(clienteRepository.save(clienteUno)).thenReturn(clienteUno);
		Cliente clienteSaved = clienteService.save(clienteUno);
		
		assertThat(clienteSaved.getNombre()).isEqualTo("cliente1");
		assertThat(clienteSaved.getCorreoElectronico()).isEqualTo("cliente1@mail.com");
		assertThat(clienteSaved.getMaximoDescubierto()).isEqualTo(BigDecimal.valueOf(15000));
		assertThat(clienteSaved.getCantObrasDisponibles()).isEqualTo(1);
		clienteSaved.setNombre("cliente1 updated");
		Mockito.when(clienteRepository.save(clienteSaved)).thenReturn(clienteSaved);
		assertThat(clienteService.update(clienteSaved).getNombre()).isEqualTo("cliente1 updated");
	}
	
	@Test
	void deleteByIdTest() {
		clienteService.deleteById(clienteUno.getId());
		verify(clienteRepository, times(1)).deleteById(clienteUno.getId());
	}
}