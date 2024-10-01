package isi.dan.msclientes.servicios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import isi.dan.msclientes.dao.UsuarioRepository;
import isi.dan.msclientes.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioUno, usuarioDos;

    @BeforeEach
    void setUp() {
        usuarioUno = new Usuario();
        usuarioUno.setId(1);
        usuarioUno.setNombre("usuario1");
        usuarioUno.setApellido("apellido1");
        usuarioUno.setCorreoElectronico("usuario1@mail.com");
        usuarioUno.setDni("12345678");

        usuarioDos = new Usuario();
        usuarioDos.setId(2);
        usuarioDos.setNombre("usuario2");
        usuarioDos.setApellido("apellido2");
        usuarioDos.setCorreoElectronico("usuario2@mail.com");
        usuarioDos.setDni("87654321");
    }

    @Test
    void findAllTest() {
        List<Usuario> usuarios = List.of(usuarioUno, usuarioDos);
        Mockito.when(usuarioRepository.findAll()).thenReturn(usuarios);
        assertEquals(usuarioService.findAll(), usuarios);
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void findByIdTest() {
        Mockito.when(usuarioRepository.findById(2)).thenReturn(Optional.of(usuarioDos));
        Optional<Usuario> u2 = usuarioService.findById(usuarioDos.getId());
        assertThat(u2).isPresent();
        assertThat(u2.get().getNombre()).isEqualTo("usuario2");
        assertThat(u2.get().getCorreoElectronico()).isEqualTo("usuario2@mail.com");
        assertThat(u2.get().getDni()).isEqualTo("87654321");
    }

    @Test
    void saveAndUpdateTest() {
        Mockito.when(usuarioRepository.save(usuarioUno)).thenReturn(usuarioUno);
        Usuario usuarioSaved = usuarioService.save(usuarioUno);

        assertThat(usuarioSaved.getNombre()).isEqualTo("usuario1");
        assertThat(usuarioSaved.getCorreoElectronico()).isEqualTo("usuario1@mail.com");
        assertThat(usuarioSaved.getDni()).isEqualTo("12345678");

        usuarioSaved.setNombre("usuario1 updated");
        Mockito.when(usuarioRepository.save(usuarioSaved)).thenReturn(usuarioSaved);
        assertThat(usuarioService.update(usuarioSaved).getNombre()).isEqualTo("usuario1 updated");
    }

    @Test
    void deleteByIdTest() {
        usuarioService.deleteById(usuarioUno.getId());
        verify(usuarioRepository, times(1)).deleteById(usuarioUno.getId());
    }
}
