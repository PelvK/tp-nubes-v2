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
    private ClienteRepository clienteRepository;

    public List<Obra> findAll() {
        return obraRepository.findAll();
    }

    public Optional<Obra> findById(Integer id) {
        return obraRepository.findById(id);
    }

    public Obra save(Obra obra) {
        return obraRepository.save(obra);
    }

    public Obra update(Obra obra) {
        return obraRepository.save(obra);
    }

    public void deleteById(Integer id) {
        obraRepository.deleteById(id);
    }

    public void asignarObraACliente(Integer clienteId, Integer obraId) throws Exception {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        Optional<Obra> obraOpt = obraRepository.findById(obraId);

        if (clienteOpt.isPresent() && obraOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            Obra obra = obraOpt.get();

            if (!obra.getEstado().equals(EstadoObra.HABILITADA)) {
                throw new Exception("La obra no está habilitada para ser asignada.");
            }

            cliente.addObra(obra);
            clienteRepository.save(cliente);
        } else {
            throw new Exception("Cliente o Obra no encontrada.");
        }
    }

    public void finalizarObra(Integer clienteId, Integer obraId) throws Exception {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        Optional<Obra> obraOpt = obraRepository.findById(obraId);


        if (clienteOpt.isPresent() && obraOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            Obra obra = obraOpt.get();

            if(obra.getEstado().equals(EstadoObra.HABILITADA)){
                obra.setEstado(EstadoObra.FINALIZADA);
                obraRepository.save(obra);

                
                Optional<Obra> obraActualizarOpt = cliente.getObras().stream()
                                            .filter(e -> EstadoObra.PENDIENTE.equals(e.getEstado()))
                                            .min(Comparator.comparing(Obra::getFecha));
                
                if (obraActualizarOpt.isPresent()) { 

                    Obra obraActualizar = obraActualizarOpt.get();
                    obraActualizar.setEstado(EstadoObra.HABILITADA);
                    obraRepository.save(obraActualizar);
                    
                }
                
            }
            else{
                throw new Exception("No se puede finalizar la obra");
            }

        } 
        else {
            throw new Exception("Cliente o Obra no encontrada.");
        }
    }

    
    public void suspenderObra(Integer clienteId, Integer obraId) throws Exception {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        Optional<Obra> obraOpt = obraRepository.findById(obraId);

        if (clienteOpt.isPresent() && obraOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            Obra obra = obraOpt.get();

            if(obra.getEstado().equals(EstadoObra.HABILITADA)){
                obra.setEstado(EstadoObra.PENDIENTE);
                obraRepository.save(obra);
            }
            else{
                throw new Exception("No se puede pasar a pendiente  la obra");
            }

        } 
        else {
            throw new Exception("Cliente o Obra no encontrada.");
        }
    }

    public void habilitarObra(Integer clienteId, Integer obraId) throws Exception {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        Optional<Obra> obraOpt = obraRepository.findById(obraId);

        if (clienteOpt.isPresent() && obraOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            Obra obra = obraOpt.get();

            if(obra.getEstado().equals(EstadoObra.HABILITADA)){
                
                Long cantObrasHabilitadas = cliente.getObras().stream()
                .filter(e -> EstadoObra.HABILITADA.equals(e.getEstado())).count();

                if(cliente.getMaximoObrasEjecucionInteger() > cantObrasHabilitadas) {
                    obra.setEstado(EstadoObra.HABILITADA);
                    obraRepository.save(obra);
                }
                else{
                    throw new Exception("No se puede pasar habilitar la obra debido a que se alcanzo el máximo de obras disponibles.");
                }
                
            }
            else{
                throw new Exception("No se puede pasar habilitar la obra");
            }

        } 
        else {
            throw new Exception("Cliente o Obra no encontrada.");
        }
    }
}

