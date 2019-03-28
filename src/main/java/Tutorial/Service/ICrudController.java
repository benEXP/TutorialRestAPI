package Tutorial.Service;

import Tutorial.Exceptions.RestException.BadRequestException;
import Tutorial.Exceptions.RestException.ConflictException;
import Tutorial.Model.Employee;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api")
public interface ICrudController {
    @PostMapping("/employees/new")
    String createEmployee(@RequestBody Employee newEmployee, @RequestHeader String headerValue) throws ConflictException;

    @GetMapping("/employees/all")
    Map<Integer, Employee> getAllEmployees(@RequestHeader(value="Token") String tokenValue);

    @GetMapping("/employees/{id}")
    Employee getEmployee(@PathVariable int id, @RequestHeader(value="Token") String tokenValue) throws BadRequestException;

    @DeleteMapping("/employees/delete/{id}")
    String deleteEmployee(@PathVariable int id, @RequestHeader(value="Token") String tokenValue);

    @PutMapping("employees/update/{id}")
    String updateEmployee(@PathVariable int id, @RequestBody Employee callEmployee, @RequestHeader(value="Token") String tokenValue) throws BadRequestException;
}
