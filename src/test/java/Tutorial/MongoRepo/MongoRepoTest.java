package Tutorial.MongoRepo;

import Tutorial.Exceptions.FileRepoExceptions.DuplicateIdException;
import Tutorial.Exceptions.FileRepoExceptions.IdNotFoundException;
import Tutorial.Exceptions.FileRepoExceptions.NoIdException;
import Tutorial.Model.Employee;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import java.net.InetSocketAddress;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class MongoRepoTest {

    private MongoRepo mongoRepo;
    private MongoClient client;
    private MongoServer server;

    @Before
    public void setUp() {
        server = new MongoServer(new MemoryBackend());

        // optionally: server.enableSsl(key, keyPassword, certificate);

        // bind on a random local port
        InetSocketAddress serverAddress = server.bind();

        client = new MongoClient(new ServerAddress(serverAddress));
        mongoRepo = new MongoRepo(client);
    }

    @After
    public void tearDown() {
        client.close();
        server.shutdown();
    }

    @Captor
    ArgumentCaptor<Employee> employeeArgumentCaptor;

    @Test
    public void createEmployee_withValidRequestBody_success() {
        Assert.assertEquals(0, mongoRepo.count()); // OK

        // Arrange
        Employee testEmployee = createTestEmployee();

        // Action
        mongoRepo.create(testEmployee);

        // Assert
        Assert.assertEquals(1, mongoRepo.count());
    }

    @Test(expected = DuplicateIdException.class)
    public void createEmployee_withExistingId_ExpectedConflict() {
        // Arrange
        Employee testEmployee1 = new Employee();
        testEmployee1.setId(1234);

        Employee testEmployee2 = new Employee();
        testEmployee2.setId(1234);

        // Action
        mongoRepo.create(testEmployee1); // OK
        mongoRepo.create(testEmployee2); // Exception
    }

    @Test(expected = NoIdException.class)
    public void createEmployee_withNoIdAttribute_expectedBadRequest(){
        // Arrange
        Employee noIdEmployee = new Employee();
        noIdEmployee.setName("NoId");
        noIdEmployee.setRole("NoIdTest");
        noIdEmployee.setAge(99);

        // Action
        mongoRepo.create(noIdEmployee);
    }

    @Test
    public void getByID_validRequest_success() {
        // Arrange
        Employee getTestEmployee = createTestEmployee();
        mongoRepo.create(getTestEmployee);

        // Action
        Employee mongoRequest = mongoRepo.getByID(getTestEmployee.getId());

        // Assert
        Assert.assertEquals(getTestEmployee.getId(), mongoRequest.getId());
        Assert.assertEquals(getTestEmployee.getAge(), mongoRequest.getAge());
        Assert.assertEquals(getTestEmployee.getName(), mongoRequest.getName());
        Assert.assertEquals(getTestEmployee.getRole(), mongoRequest.getRole());
    }

    @Test(expected = IdNotFoundException.class)
    public void getByID_nonExistingId_expectedIdNotFoundException() {
        // Arrange
        int notValidId = 123;

        // Action
        Employee mongoRequest = mongoRepo.getByID(notValidId);
    }

    @Test
    public void getAllEmployee_withValidRequest_success() {
        // Arrange
        Employee testEmployee1 = createTestEmployee();
        mongoRepo.create(testEmployee1);

        // Assert
        Assert.assertEquals(1, mongoRepo.getAll().size());
    }

    @Test
    public void updateEmployee_withValidRequest_success() {
        // Arrange
        Employee testEmployee = createTestEmployee();
        mongoRepo.create(testEmployee);

        // Action
        Employee updateEmployee = mongoRepo.getByID(testEmployee.getId());
        updateEmployee.setName("update");
        updateEmployee.setRole("update Role");
        updateEmployee.setAge(34);

        mongoRepo.update(updateEmployee);

        Employee requestResult = mongoRepo.getByID(updateEmployee.getId());

        // Assert
        Assert.assertEquals(updateEmployee.getId(), requestResult.getId());
        Assert.assertEquals(updateEmployee.getAge(), requestResult.getAge());
        Assert.assertEquals(updateEmployee.getName(), requestResult.getName());
        Assert.assertEquals(updateEmployee.getRole(), requestResult.getRole());
    }

    @Test(expected = NullPointerException.class)
    public void updateEmployee_withInValidId_ExpectedNullPointerException() {
        // Arrange
        Employee inValidEmployee = createTestEmployee();

        // Action
        mongoRepo.update(inValidEmployee);
    }

    @Test
    public void deleteById_withExistingId_success(){
        // Arrange
        Employee deleteEmployee = createTestEmployee();
        mongoRepo.create(deleteEmployee);
        Assert.assertEquals(1, mongoRepo.count());

        // Action
        mongoRepo.deleteByID(deleteEmployee.getId());

        // Assert
        Assert.assertEquals(0, mongoRepo.count());
    }

    @Test(expected = IdNotFoundException.class)
    public void deleteByID_withNonExistingId_expectedIdNotFoundException() {
        // Arrange
        Employee deletEmployee = createTestEmployee();
        Assert.assertEquals(0, mongoRepo.count());

        // Action
        mongoRepo.getByID(deletEmployee.getId());
    }

    private Employee createTestEmployee() {
        Employee testEmployee = new Employee();
        testEmployee.setId(2873);
        testEmployee.setName("MockTestData");
        testEmployee.setAge(99);
        testEmployee.setRole("MockData");

        return testEmployee;
    }

}