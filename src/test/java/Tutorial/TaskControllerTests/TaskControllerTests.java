package Tutorial.TaskControllerTests;

import Tutorial.Exceptions.FileRepoExceptions.DuplicateIdException;
import Tutorial.Exceptions.FileRepoExceptions.IdNotFoundException;
import Tutorial.Exceptions.FileRepoExceptions.NoIdException;
import Tutorial.Exceptions.RestException.BadRequestException;
import Tutorial.Exceptions.RestException.ConflictException;
import Tutorial.Exceptions.RestException.UnauthorizedException;
import Tutorial.FileRepo.Repo;
import Tutorial.Model.Employee;
import Tutorial.Model.TokenList;
import Tutorial.Service.CrudController;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TaskControllerTests {

    private int notExistingID = 1111;
    private String defaultToken = "default";
    private String inValidToken = "InvalidToken";
    private List mockList = Arrays.asList("default");

    @Mock
    Repo mockRepo;

    @Mock
    TokenList mockToken;

    @Captor
    ArgumentCaptor<Employee> argumentCaptor;

    @Captor
    ArgumentCaptor<Integer> integerArgumentCaptor;

    @InjectMocks
    CrudController crudController;

    @Test
    public void createEmployee_withValidRequestBody_createdSuccessfully() {
        // Arrange
        Employee newEmployee = createTestEmployee();

        // Action
        mockRepo.create(newEmployee);

        // Assert
        Mockito.verify(mockRepo).create(argumentCaptor.capture());
        Assert.assertEquals(newEmployee, argumentCaptor.getValue());
    }

    @Test(expected = UnauthorizedException.class)
    public void createEmployee_withInValidToken_expectedUnauthorizedException() {
        // Arrange
        Employee newEmployee = createTestEmployee();

        // Action
        when(mockToken.getTokenList()).thenReturn(mockList);
        crudController.createEmployee(newEmployee, inValidToken);
    }

    @Test(expected = NoIdException.class)
    public void createEmployee_withNoIdAttribute_expectedBadRequest(){
        // Arrange
        Employee noIdEmployee = new Employee();
        noIdEmployee.setName("NoId");
        noIdEmployee.setRole("NoIdTest");
        noIdEmployee.setAge(99);

        // Action
        Mockito.doThrow(new NoIdException("ID is empty or 0")).when(mockRepo).create(noIdEmployee);
        when(mockToken.getTokenList()).thenReturn(mockList);
        crudController.createEmployee(noIdEmployee, defaultToken);
    }

    @Test(expected = ConflictException.class)
    public void createEmployee_withSameID_expectedConflict(){
        // Arrange
        Employee duplicate = createTestEmployee();

        // Action
        Mockito.doThrow(new DuplicateIdException("Same Id")).when(mockRepo).create(duplicate);
        when(mockToken.getTokenList()).thenReturn(mockList);
        crudController.createEmployee(duplicate, defaultToken);
    }

    @Test
    public void deleteEmployee_withExistingId_deleteSuccessfully(){
        // Arrange
        Employee deleteEmployee = createTestEmployee();

        // Action
        mockRepo.deleteByID(deleteEmployee.getId());
        Mockito.verify(mockRepo).deleteByID(integerArgumentCaptor.capture());

        // Assert
        Mockito.verify(mockRepo, times(1)).deleteByID(deleteEmployee.getId());
        Assert.assertEquals(deleteEmployee.getId(), integerArgumentCaptor.getValue().intValue());
    }

    @Test(expected = IdNotFoundException.class)
    public void deleteEmployee_withNonExistingId_expectedBadRequest() {
        // Arrange
        Employee noIdEmployee = new Employee();

        // Action
        Mockito.doThrow(new IdNotFoundException("No such employee")).when(mockRepo).deleteByID(noIdEmployee.getId());
        when(mockToken.getTokenList()).thenReturn(mockList);

        // Assert
        crudController.deleteEmployee(noIdEmployee.getId(), defaultToken);
    }

    @Test(expected = UnauthorizedException.class)
    public void deleteEmployee_withNonExistingId_expectedUnAuthorized() {
        // Arrange
        Employee noIdEmployee = new Employee();

        // Action
        when(mockToken.getTokenList()).thenReturn(mockList);

        // Assert
        crudController.deleteEmployee(noIdEmployee.getId(), inValidToken);
    }

    @Test
    public void getEmployee_withExistingId_successfullyGetEmployee() {
        // Arrange
        Employee testEmployee = createTestEmployee();

        // Action
        when(mockRepo.getByID(testEmployee.getId())).thenReturn(testEmployee);
        when(mockToken.getTokenList()).thenReturn(mockList);

        // Assert
        Assert.assertEquals(testEmployee, crudController.getEmployee(testEmployee.getId(), defaultToken));
    }

    @Test(expected = UnauthorizedException.class)
    public void getEmployee_withValidToken_expectedUnAuthorizedException() {
        // Arrange
        Employee testEmployee = createTestEmployee();

        // Action
        when(mockToken.getTokenList()).thenReturn(mockList);

        // Assert
        Assert.assertEquals(testEmployee, crudController.getEmployee(testEmployee.getId(), inValidToken));
    }

    @Test(expected = BadRequestException.class)
    public void getEmployee_withNonExistingId_expectedBadRequest() {
        // Action
        when(mockRepo.getByID(notExistingID)).thenThrow(new IdNotFoundException("No such ID"));
        when(mockToken.getTokenList()).thenReturn(mockList);

        // Assert
        Assert.assertEquals(new IdNotFoundException[]{new IdNotFoundException("No such ID")}, crudController.getEmployee(notExistingID, defaultToken));
    }

    @Test
    public void getAllEmployees_withValidToken_Success() {
        // Arrange
        Map<Integer, Employee> mockMap = new HashMap<>();
        mockMap.put(1, createTestEmployee());
        mockMap.put(2, createTestEmployee());

        // Action
        when(mockRepo.getAll()).thenReturn(mockMap);
        when(mockToken.getTokenList()).thenReturn(mockList);

        // Assert
        Assert.assertEquals(2, crudController.getAllEmployees(defaultToken).size());
    }

    @Test(expected = UnauthorizedException.class)
    public void getAllEmployees_withInValidToken_expectedUnauthorized() {
        // Arrange
        Map<Integer, Employee> mockMap = new HashMap<>();
        mockMap.put(1, createTestEmployee());
        mockMap.put(2, createTestEmployee());

        when(mockToken.getTokenList()).thenReturn(mockList);

        // Assert
        Assert.assertEquals(2, crudController.getAllEmployees(inValidToken).size());
    }

    @Test
    public void updateEmployee_withExistingId_successfullyUpdated() {
        // Arrange
        Employee testEmployee = createTestEmployee();

        // Action
        mockRepo.update(testEmployee);
        Mockito.verify(mockRepo).update(argumentCaptor.capture());

        // Assert
        Mockito.verify(mockRepo, times(1)).update(testEmployee);
        Assert.assertEquals(testEmployee, argumentCaptor.getValue());
    }

    @Test(expected = BadRequestException.class)
    public void updateEmployee_withNonExistingId_expectedBadRequest() {
        // Arrange
        Employee nonExistingEmployee = createTestEmployee();
        when(mockToken.getTokenList()).thenReturn(mockList);

        // Assert
        crudController.updateEmployee(nonExistingEmployee.getId(), nonExistingEmployee, defaultToken);

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
