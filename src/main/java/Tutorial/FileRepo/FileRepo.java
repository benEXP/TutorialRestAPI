package Tutorial.FileRepo;

import Tutorial.Exceptions.FileRepoExceptions.DuplicateIdException;
import Tutorial.Exceptions.FileRepoExceptions.IdNotFoundException;
import Tutorial.Exceptions.FileRepoExceptions.NoIdException;
import Tutorial.Model.Employee;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileRepo implements Repo {
    private File fileObject;
    private Map<Integer, Employee> employees;
    private ObjectMapper objectMapper = new ObjectMapper();

    public FileRepo(File fileObjectCheck) throws IOException {
        if(!(fileObjectCheck.isFile() && fileObjectCheck.exists())) {
            throw new NullPointerException("Instance is null.\n");
        }
        this.fileObject = fileObjectCheck;
        this.employees = getEmployeesInstance();
    }

    public void create(Employee employee) {
        if (employee.getId() == 0) {
            throw new NoIdException("ID cannot be 0/empty");
        }

        if(isEmployeeExist(employee.getId())) {
            throw new DuplicateIdException("Same ID Existed");
        }

        try {
            this.employees.put(employee.getId(), employee);
            saveToFile();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public Employee getByID(int id){
        if(!isEmployeeExist(id)) {
            throw new IdNotFoundException("no such ID");
        }
        return this.employees.get(id);
    }

    public Map<Integer, Employee> getAll() {
        return this.employees;
    }

    public void update(Employee employee)  {
        if(!isEmployeeExist(employee.getId())) {
            throw new NullPointerException("No such ID");
        }
        this.employees.replace(employee.getId(), employee);

        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteByID(int id) {
        if(!isEmployeeExist(id)) {
            throw new IdNotFoundException("ID not found");
        }
        this.employees.remove(id);

        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isEmployeeExist(int id){
        try {
            this.employees = getEmployeesInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.employees.containsKey(id);
    }

    private Map<Integer, Employee> getEmployeesInstance() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileObject.getAbsolutePath()))) {
            String jsonString = IOUtils.toString(br);
            return jsonString.isEmpty() ?
                    new HashMap<>() : objectMapper.readValue(jsonString, new TypeReference<Map<Integer, Employee>>(){});
        }
    }

    private void saveToFile() throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileObject.getAbsolutePath()));
        String jsonSerialize = objectMapper.writeValueAsString(employees);
        bufferedWriter.write(jsonSerialize);
        bufferedWriter.close();
    }
}
