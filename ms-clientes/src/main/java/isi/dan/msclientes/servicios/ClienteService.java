package isi.dan.msclientes.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import isi.dan.msclientes.dao.ClienteRepository;
import isi.dan.msclientes.model.Cliente;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;

    @Value("${ms-clientes.max-cant-obras-disponibles:1}")
	private Integer cantObrasDisponibles;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public List<Cliente> findByFilters(String cuit, String nombre) {
        List<Cliente> clientes = clienteRepository.findAll();
        if (cuit != null) {
            clientes = clientes.stream().filter(cliente -> cliente.getCuit().toUpperCase().contains(cuit.toUpperCase())).toList();
        }
        if (nombre != null) {
            clientes = clientes.stream().filter(cliente -> cliente.getNombre().toUpperCase().contains(nombre.toUpperCase())).toList();
        }
        return clientes;
    }

    public Optional<Cliente> findById(Integer id) {
        return clienteRepository.findById(id);
    }

    public Cliente save(Cliente cliente) {
        cliente.setCantObrasDisponibles(cantObrasDisponibles);
        return clienteRepository.save(cliente);
    }

    public Cliente update(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deleteById(Integer id) {
        clienteRepository.deleteById(id);
    }
}
