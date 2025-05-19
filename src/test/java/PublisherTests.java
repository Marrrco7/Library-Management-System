
import org.example.model.Publisher;
import org.example.service.PublisherService;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PublisherTests {

    private EntityManagerFactory emf;
    private PublisherService publisherService;

    @BeforeAll
    void setUpAll() {
        emf = Persistence.createEntityManagerFactory("testPU");
        publisherService = new PublisherService("testPU");
    }

    @BeforeEach
    void cleanDatabase() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Publisher").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @AfterAll
    void tearDownAll() {
        if (emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    void testCreatePublisher() {
        publisherService.createPublisher("Acme Publishing", "123 Main St", "555-1234");

        List<Publisher> publishers = publisherService.getAllPublishers();
        assertEquals(1, publishers.size(), "There should be exactly one publisher in DB.");

        Publisher p = publishers.get(0);
        assertNotNull(p.getId(), "Publisher ID should be generated.");
        assertEquals("Acme Publishing", p.getName());
        assertEquals("123 Main St", p.getAddress());
        assertEquals("555-1234", p.getPhoneNumber());
    }

    @Test
    void testGetAllPublishers() {
        publisherService.createPublisher("PubOne", "Address1", "Phone1");
        publisherService.createPublisher("PubTwo", "Address2", "Phone2");

        List<Publisher> publishers = publisherService.getAllPublishers();
        assertEquals(2, publishers.size(), "Should have exactly 2 publishers.");

    }

    @Test
    void testReadPublisherById() {
        publisherService.createPublisher("TestPub", "Some Address", "000-1111");
        Publisher created = publisherService.getAllPublishers().get(0);

        Publisher retrieved = publisherService.getPublisherById(created.getId());
        assertNotNull(retrieved);
        assertEquals("TestPub", retrieved.getName());
        assertEquals("Some Address", retrieved.getAddress());
        assertEquals("000-1111", retrieved.getPhoneNumber());
    }

    @Test
    void testUpdatePublisher() {
        // Create a publisher
        publisherService.createPublisher("OldName", "OldAddress", "OldPhone");
        Publisher publisher = publisherService.getAllPublishers().get(0);
        int pubId = publisher.getId();

        publisherService.updatePublisher(pubId, "NewName", "NewAddress", "NewPhone");


        Publisher updated = publisherService.getPublisherById(pubId);
        assertEquals("NewName", updated.getName());
        assertEquals("NewAddress", updated.getAddress());
        assertEquals("NewPhone", updated.getPhoneNumber());
    }

    @Test
    void testDeletePublisher() {
        publisherService.createPublisher("DeleteMe", "123 Delete St", "555-9999");
        Publisher publisher = publisherService.getAllPublishers().get(0);
        int pubId = publisher.getId();

        publisherService.deletePublisher(pubId);

        Publisher deleted = publisherService.getPublisherById(pubId);
        assertNull(deleted, "Publisher should be removed from the database.");
    }
}
