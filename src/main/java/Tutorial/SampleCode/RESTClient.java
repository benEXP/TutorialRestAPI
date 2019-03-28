package Tutorial.SampleCode;

import Tutorial.Model.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class RESTClient {
    private ObjectMapper objectMapper;
    private final String tokenKey;

    public RESTClient() {
        this.objectMapper = new ObjectMapper();
        this.tokenKey = "Token";
    }

    public Employee getEmployeeById(int id, String tokenValue) {
        final String uri = "http://localhost:8080/api/employees/{id}";

        Map<String, Integer> params = new HashMap<>();
        params.put("id", id);

        HttpHeaders headers = new HttpHeaders();
        headers.set(tokenKey, tokenValue);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<Employee> result = restTemplate.exchange(uri, HttpMethod.GET, entity, Employee.class, params);

        Employee ss =  result.getBody();
        return ss;
    }

    public String createEmployee(Employee employee, String tokenValue) throws JsonProcessingException {
        final String uri = "http://localhost:8080/api/employees/new";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set(tokenKey, tokenValue);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonEmployee = this.objectMapper.writeValueAsString(employee);

        HttpEntity<String> entity = new HttpEntity<>(jsonEmployee, headers);
        return restTemplate.exchange(uri, HttpMethod.POST, entity, String.class).getBody();
    }

    public String deleteEmployee(int id,String tokenValue) {
        final String uri = "http://localhost:8080/api/employees/delete/{id}";

        Map<String, Integer> params = new HashMap<>();
        params.put("id", id);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", tokenValue);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class, params).getBody();
    }

    public String updateEmployee(Employee employee, String tokenValue) throws JsonProcessingException {
        final String uri = "http://localhost:8080/api/employees/update/{id}";

        Map<String, Integer> params = new HashMap<>();
        params.put("id", employee.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", tokenValue);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonEmployee = this.objectMapper.writeValueAsString(employee);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(jsonEmployee, headers);

        return restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class, params).getBody();
    }

    public String getAllEmployee(String tokenValue) {
        final String uri = "http://localhost:8080/api/employees/all";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", tokenValue);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(uri, HttpMethod.GET, entity, String.class).getBody();
    }

}
