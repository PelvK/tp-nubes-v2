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

import isi.dan.msclientes.model.Obra;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
@ActiveProfiles("db")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ObraRepositoryTest {

    Logger log = LoggerFactory.getLogger(ObraRepositoryTest.class);

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withReuse(true)
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private ObraRepository obraRepository;

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
        Obra obra = new Obra();
		obra.setId(1);
		obra.setDireccion("Direccion Test Obra");
		obra.setLat(-12.5f);
		obra.setLng(21.7f);
		obra.setPresupuesto(BigDecimal.valueOf(105));
		cliente = new Cliente();
		cliente.setId(1);
		cliente.setNombre("Test Cliente");
		cliente.setCorreoElectronico("test@cliente.com");
		cliente.setMaximoDescubierto(BigDecimal.valueOf(100000));
        obra.setFecha(Date.valueOf(LocalDate.of(2024, 8, 10)));
		cliente = clienteRepository.save(cliente);
		obra.setCliente(cliente);
        obraRepository.save(obra);
    }

    @BeforeAll
    public static void beforeAll() {
    	mysqlContainer.start();
    }

    @BeforeEach
    void borrarDatos(){
        obraRepository.deleteAll();
    }

    @AfterAll
    static void stopContainer() {
        mysqlContainer.stop();
    }

    @Test
    void testSaveAndFindById() {
        Obra obra = new Obra();
		obra.setId(2);
		obra.setDireccion("Dir test");
		obra.setLat(0);
		obra.setLng(0);
		obra.setPresupuesto(BigDecimal.valueOf(150));
		obra.setCliente(cliente);
        obra.setFecha(Date.valueOf(LocalDate.of(2016,5, 10)));
        obra = obraRepository.save(obra);

        Optional<Obra> foundObra = obraRepository.findById(obra.getId());
        log.info("ENCONTRE: {} ",foundObra);
        assertThat(foundObra).isPresent();
        assertThat(foundObra.get().getDireccion()).isEqualTo("Dir test");
        assertThat(foundObra.get().getLat()).isEqualTo(0);
        assertThat(foundObra.get().getLng()).isEqualTo(0);
        assertThat(foundObra.get().getPresupuesto()).isEqualTo(BigDecimal.valueOf(150));
    }

    @Test
    void testFindByPresupuesto() {
        Obra obra = new Obra();
		obra.setId(2);
		obra.setDireccion("Dir test");
		obra.setPresupuesto(BigDecimal.valueOf(150));
		obra.setCliente(cliente);
        obra.setFecha(Date.valueOf(LocalDate.of(2016,6, 10)));
        obraRepository.save(obra);

        List<Obra> resultado = obraRepository.findByPresupuestoGreaterThanEqual(BigDecimal.valueOf(50));
        log.info("ENCONTRE: {} ",resultado);
        assertThat(resultado.size()).isEqualTo(2);
        assertThat(resultado.get(0).getPresupuesto()).isGreaterThan(BigDecimal.valueOf(50));
        assertThat(resultado.get(1).getPresupuesto()).isGreaterThan(BigDecimal.valueOf(50));
    }

}