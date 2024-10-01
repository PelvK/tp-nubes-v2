package isi.dan.msclientes.dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import isi.dan.msclientes.model.Usuario;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
@ActiveProfiles("db")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    Logger log = LoggerFactory.getLogger(UsuarioRepositoryTest.class);

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withReuse(true)
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @BeforeEach
    void iniciarDatos(){
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        usuario.setCorreoElectronico("juan.perez@test.com");
        usuario.setDni("12345678");

        usuarioRepository.save(usuario);
    }

    @BeforeAll
    public static void beforeAll() {
        mysqlContainer.start();
    }

    @BeforeEach
    void borrarDatos(){
        usuarioRepository.deleteAll();
    }

    @AfterAll
    static void stopContainer() {
        mysqlContainer.stop();
    }

    @Test
    void testSaveAndFindById() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Carlos");
        usuario.setApellido("Lopez");
        usuario.setCorreoElectronico("carlos.lopez@test.com");
        usuario.setDni("87654321");
        usuario = usuarioRepository.save(usuario);

        Optional<Usuario> foundUsuario = usuarioRepository.findById(usuario.getId());
        log.info("ENCONTRE: {} ", foundUsuario);
        assertThat(foundUsuario).isPresent();
        assertThat(foundUsuario.get().getNombre()).isEqualTo("Carlos");
        assertThat(foundUsuario.get().getApellido()).isEqualTo("Lopez");
        assertThat(foundUsuario.get().getCorreoElectronico()).isEqualTo("carlos.lopez@test.com");
        assertThat(foundUsuario.get().getDni()).isEqualTo("87654321");
    }

    @Test
    void testFindByCorreoElectronico() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setApellido("Garcia");
        usuario.setCorreoElectronico("ana.garcia@test.com");
        usuario.setDni("11223344");
        usuarioRepository.save(usuario);

        List<Usuario> usuarios = usuarioRepository.findByCorreoElectronico("ana.garcia@test.com");
        log.info("ENCONTRE: {} ", usuarios);
        assertThat(usuarios.size()).isEqualTo(1);
        assertThat(usuarios.get(0).getCorreoElectronico()).isEqualTo("ana.garcia@test.com");
    }

    @Test
    void testFindByDni() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Lucia");
        usuario.setApellido("Martinez");
        usuario.setCorreoElectronico("lucia.martinez@test.com");
        usuario.setDni("44556677");
        usuarioRepository.save(usuario);

        Optional<Usuario> foundUsuario = usuarioRepository.findByDni("44556677");
        log.info("ENCONTRE: {} ", foundUsuario);
        assertThat(foundUsuario).isPresent();
        assertThat(foundUsuario.get().getDni()).isEqualTo("44556677");
    }
}
