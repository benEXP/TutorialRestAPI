package Tutorial.FileRepo;

import Tutorial.Exceptions.FileRepoExceptions.DuplicateIdException;
import Tutorial.Exceptions.FileRepoExceptions.IdNotFoundException;
import Tutorial.Exceptions.FileRepoExceptions.NoIdException;
import Tutorial.Model.Employee;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Map;

public class FileRepoTest {

    private int validUniqueID;
    private File notValidJson =  new File(".\\NotValidJson.txt");
    private File validFileObject =  new File(".\\TestingJSON.txt");
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        this.validUniqueID = 212339;
        this.mapper = new ObjectMapper();

        overwriteWithValidJson(validFileObject);
        overwriteWithInvalidJson(notValidJson);
    }

    @After
    public void cleanUp() {
        clearFile(validFileObject);
    }

    @Test
    public void create_withUniqueID_createdSuccessfully() throws IOException {

        // Arrange
        File fileObject =  new File(".\\test.txt");
        clearFile(fileObject);
        ValidateFileObjectCount(fileObject, 0);

        // Act
        Employee employee = new Employee();
        employee.setId(validUniqueID);
        FileRepo repo = new FileRepo(fileObject);
        repo.create(employee);

        // Assert
        Assert.assertNotNull(repo);
        ValidateFileObjectCount(fileObject, 1);
    }

    @Test(expected = NoIdException.class)
    public void create_withoutSettingUniqueID_expectedNullPointerException() throws IOException {
        // Arrange
        File fileObject =  new File(".\\test.txt");
        clearFile(fileObject);
        ValidateFileObjectCount(fileObject, 0);

        // Act
        Employee employee = new Employee();
        FileRepo repo = new FileRepo(fileObject);
        repo.create(employee);
    }

    @Test(expected = DuplicateIdException.class)
    public void create_withSameUniqueID_expectedDuplicateIdExceptionOn2ndCreate() throws IOException {
        // Arrange
        Employee employee1 = new Employee();
        employee1.setId(111);

        Employee employee2 = new Employee();
        employee2.setId(111);

        // Act
        FileRepo repo = new FileRepo(validFileObject);
        repo.create(employee1); // OK
        ValidateFileObjectCount(validFileObject, 1);

        repo.create(employee2); // Exception
    }

    @Test
    public void getByID_withExistingID_getSuccessfully() throws IOException {
        // Arrange
        int validID = 12345;
        int validAge = 36;
        String validName = "TestGetID";
        String validRole = "Tester";

        Employee expected = new Employee();
        expected.setId(validID);
        expected.setName(validName);
        expected.setAge(validAge);
        expected.setRole(validRole);

        // Act
        FileRepo repo = new FileRepo(validFileObject);
        repo.create(expected);
        Employee actual = repo.getByID(validID);

        // Assert
        ValidateFileObjectCount(validFileObject, 1);
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getAge(), actual.getAge());
        Assert.assertEquals(expected.getRole(), actual.getRole());
    }

    @Test
    public void getByID_withTwoExistingID_getSuccessfully() throws IOException {
        // Arrange
        Employee employee = new Employee();
        employee.setId(12345);

        Employee employee2 = new Employee();
        employee2.setId(22222);

        FileRepo repo = new FileRepo(validFileObject);
        repo.create(employee); // create OK
        repo.create(employee2); // create OK

        // Act
        Employee employeeTest1 = repo.getByID(employee.getId());
        Employee employeeTest2 = repo.getByID(employee2.getId());

        // Assert
        ValidateFileObjectCount(validFileObject, 2);
        Assert.assertEquals(employee.getId(), employeeTest1.getId());
        Assert.assertEquals(employee2.getId(), employeeTest2.getId());
    }

    @Test(expected = IdNotFoundException.class)
    public void getByID_withNonExistingID_expectedNullPointerException() throws IOException {
        // Arrange
        int inValidId = 33333;

        FileRepo repo = new FileRepo(validFileObject);

        Employee notValidEmployee = new Employee();
        notValidEmployee.setId(555555);
        repo.create(notValidEmployee);

        // Act
        repo.getByID(inValidId);
    }

    @Test
    public void getAll_validJsonObject_getAllEmployees() throws IOException {
        // Arrange
        int firstId = 111;
        int secondId = 222;
        int thirdId  = 333;

        Employee employee1 = new Employee();
        Employee employee2 = new Employee();
        Employee employee3 = new Employee();

        employee1.setId(firstId);
        employee2.setId(secondId);
        employee3.setId(thirdId);

        FileRepo repo =  new FileRepo(validFileObject);
        repo.create(employee1);
        repo.create(employee2);
        repo.create(employee3);

        // Act\
        Map<Integer, Employee> mapList = repo.getAll();

        // Assert
        Assert.assertEquals(3, mapList.size());
        Assert.assertTrue(isContainsId(mapList, firstId));
        Assert.assertTrue(isContainsId(mapList, secondId));
        Assert.assertTrue(isContainsId(mapList, thirdId));
    }

    @Test
    public void update_withExistingObject_updateSuccessfully() throws IOException {
        // Arrange
        int initialAge = 33;
        int initialId = 88888;
        String initialRole = "BeforeUpdate";
        String initialName = "TestNameUpdate";

        int updateAge = 77;
        String updateRole = "AfterUpdate";
        String updateName = "TestAfterUpdate";

        Employee employee = new Employee();
        employee.setId(initialId);
        employee.setAge(initialAge);
        employee.setRole(initialRole);
        employee.setName(initialName);

        FileRepo repo = new FileRepo(validFileObject);
        repo.create(employee);

        // Act
        Employee updatedEmployee = repo.getByID(employee.getId());
        updatedEmployee.setAge(updateAge);
        updatedEmployee.setRole(updateRole);
        updatedEmployee.setName(updateName);
        repo.update(updatedEmployee);

        // Assert
        Employee testEmployee = repo.getByID(updatedEmployee.getId());
        Assert.assertEquals(updateAge, testEmployee.getAge());
        Assert.assertEquals(updateName, testEmployee.getName());
        Assert.assertEquals(updateRole, testEmployee.getRole());
    }

    @Test
    public void deleteByID_withExistingObject_deletedSuccessfully() throws IOException {
        // Arrange
        int uniqueId = 909090;

        Employee employee = new Employee();
        employee.setId(uniqueId);

        FileRepo repo = new FileRepo(validFileObject);
        repo.create(employee);

        Assert.assertEquals(1, getEmployeeSize(validFileObject));

        // Act
        repo.deleteByID(uniqueId);

        // Assert
        Assert.assertEquals(0, getEmployeeSize(validFileObject));
    }

    @Test(expected = IdNotFoundException.class)
    public void deleteByID_NonExistingObject_expectedNullPointerException() throws IOException {
        // Arrange
        int validId = 909090;
        int inValidId = 606060;

        Employee employee = new Employee();
        employee.setId(validId);

        FileRepo repo =  new FileRepo(validFileObject);
        repo.create(employee);

        // Act
        repo.deleteByID(inValidId);
    }

    @Test
    public void getAll_directlyFromFileRepo_successfullyObtainedAllEmployees() throws IOException {
        // Arrange
        FileRepo repo =  new FileRepo(new File(".\\MockTest.txt"));

        // Act
        Map<Integer, Employee> mapList = repo.getAll();

        // Assert
        Assert.assertEquals(1, mapList.size());
    }

    private void ValidateFileObjectCount(File fileObject, int count) {
        Assert.assertEquals(getEmployeeSize(fileObject), count);
    }

    private int getEmployeeSize(File validFileObject) {
        try {
            if(!validFileObject.exists()) {
                validFileObject.createNewFile();
            }

            FileReader fr = new FileReader(validFileObject);
            BufferedReader br = new BufferedReader(fr);
            String testJson = br.readLine();
            Map<Integer, Employee> jsonDeserialize = mapper.readValue(testJson, new TypeReference<Map<Integer, Employee>>(){});
            int size = jsonDeserialize.size();
            return size;
        }
        catch (Exception exc){
            return 0;
        }
    }

    private void overwriteWithValidJson(File fileObject) {
        try {
            String value = "";
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileObject.getAbsolutePath()));
            bufferedWriter.write(value);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void overwriteWithInvalidJson(File fileObject){
        try {
            String value = "[        \"test\" : \"123\" ]";
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileObject.getAbsolutePath()));
            bufferedWriter.write(value);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearFile(File fileObject) {
        if(fileObject == null) {
            return;
        }

        try {
            FileWriter fw = new FileWriter(fileObject.getAbsolutePath());
            fw.write("");
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isContainsId(Map<Integer, Employee> mapList, int id) {
        return mapList.containsKey(id);
    }

}