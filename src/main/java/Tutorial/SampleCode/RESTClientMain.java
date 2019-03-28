package Tutorial.SampleCode;

import Tutorial.Model.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;

public class RESTClientMain {
    public static void main(String[] args) throws JsonProcessingException {
        String validToken = "default";
        String inValidtoken = "wrongToken";

        RESTClient restClient = new RESTClient();

        // Setting a default new Employee Object
        Employee newEmployee = new Employee();
        newEmployee.setId(7890);
        newEmployee.setName("Sample");
        newEmployee.setAge(66);
        newEmployee.setRole("RestCall");

        // Calling create Employee endpoint
        System.out.println(restClient.createEmployee(newEmployee, validToken));
//        System.out.println(restClient.createEmployee(newEmployee, inValidtoken));

        // Calling getByID endpoint
        Employee myGetEmployee = restClient.getEmployeeById(7890, validToken);
//        Employee myGetEmployee = restClient.getEmployeeById(7890, inValidtoken);
        System.out.println(myGetEmployee.getId());
        System.out.println(myGetEmployee.getName());
        System.out.println(myGetEmployee.getAge());
        System.out.println(myGetEmployee.getRole());

        // Calling updateEmployee endpoint
        myGetEmployee.setName("Update Sample");
        myGetEmployee.setRole("Update Endpoint");
        myGetEmployee.setAge(88);
        System.out.println(restClient.updateEmployee(myGetEmployee, validToken));
//        System.out.println(restClient.updateEmployee(myGetEmployee, inValidtoken));

        // Calling deleteEmployee endpoint
        System.out.println(restClient.deleteEmployee(7890, validToken));
//        System.out.println(restClient.deleteEmployee(7890, inValidtoken));

        //Calling getAllEmployees endpoint
        System.out.println(restClient.getAllEmployee(validToken));
//        System.out.println(restClient.getAllEmployee(inValidtoken));
    }
}
