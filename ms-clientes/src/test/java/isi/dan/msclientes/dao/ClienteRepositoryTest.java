package isi.dan.msclientes.dao;

import org.junit.jupiter.api.AfterAll;
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

import isi.dan.msclientes.model.Cliente;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
@ActiveProfiles("db")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClienteRepositoryTest {

    Logger log = LoggerFactory.getLogger(ClienteRepositoryTest.class);

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

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
        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente Test");
        cliente.setCorreoElectronico("cliente@test.com");
        cliente.setCuit("20-12345678-9");
        cliente.setMaximoDescubierto(BigDecimal.valueOf(5000));
        clienteRepository.save(cliente);
    }

    @BeforeEach
    void borrarDatos(){
        clienteRepository.deleteAll();
    }

    @AfterAll
    static void stopContainer() {
        mysqlContainer.stop();
    }

    @Test
    void testSaveAndFindById() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente Nuevo");
        cliente.setCorreoElectronico("nuevo@test.com");
        clienteRepository.save(cliente);

        Optional<Cliente> foundCliente = clienteRepository.findById(cliente.getId());
        log.info("ENCONTRE: {} ", foundCliente);
        assertThat(foundCliente).isPresent();
        assertThat(foundCliente.get().getNombre()).isEqualTo("Cliente Nuevo");
    }
}
