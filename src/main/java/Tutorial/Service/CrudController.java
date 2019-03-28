package Tutorial.Service;

import Tutorial.Exceptions.FileRepoExceptions.DuplicateIdException;
import Tutorial.Exceptions.FileRepoExceptions.IdNotFoundException;
import Tutorial.Exceptions.RestException.BadRequestException;
import Tutorial.Exceptions.RestException.ConflictException;
import Tutorial.Exceptions.RestException.UnauthorizedException;
import Tutorial.FileRepo.Repo;
import Tutorial.Model.Employee;
import Tutorial.Model.TokenList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class CrudController implements ICrudController {

    private Repo repo;
    private TokenList tokenList;
    private String unAuthorized = "Not Authorized";

    @Autowired
    private CrudController(Repo repo, TokenList tokenList) {
        this.repo = repo;
        this.tokenList = tokenList;
    }

    @PostMapping("/employees/new")
    public String createEmployee(@RequestBody Employee newEmployee, @RequestHeader(value="Token") String tokenValue) {
        if(!isValidToken(tokenValue)) {
            throw new UnauthorizedException(unAuthorized);
        }
        try {
            repo.create(newEmployee);
            return "Created " + newEmployee.getId() + " successfully";
        } catch (NullPointerException ne) {
            throw new BadRequestException("ID cannot be null");
        } catch (DuplicateIdException de) {
            throw new ConflictException("Same ID existed");
        }
    }

    @Override
    @GetMapping("/employees/all")
    public Map<Integer, Employee> getAllEmployees(@RequestHeader(value="Token") String tokenValue) {
        if(!isValidToken(tokenValue)) {
            throw new UnauthorizedException(unAuthorized);
        }
        return repo.getAll();
    }

    @Override
    @GetMapping("/employees/{id}")
    public Employee getEmployee(@PathVariable int id, @RequestHeader(value="Token") String tokenValue) {
        if(!isValidToken(tokenValue)) {
            throw new UnauthorizedException(unAuthorized);
        }
        try {
            return getEmployee(id);
        } catch (IdNotFoundException e) {
            throw new BadRequestException("Id not Found");
        }
    }

    private Employee getEmployee(int id) {
        return repo.getByID(id);
    }

    @Override
    @DeleteMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable int id, @RequestHeader(value="Token") String tokenValue) {
        if(!isValidToken(tokenValue)) {
            throw new UnauthorizedException(unAuthorized);
        }
        repo.deleteByID(id);
        return "Deleted "+ id +" successfully";
    }

    @Override
    @PutMapping("employees/update/{id}")
    public String updateEmployee(@PathVariable int id, @RequestBody Employee callEmployee, @RequestHeader(value="Token") String tokenValue) {
        if(!isValidToken(tokenValue)) {
            throw new UnauthorizedException(unAuthorized);
        }

        if( id != callEmployee.getId()) {
            return "Id not matched!";
        }

        try {
            Employee employee = getEmployee(callEmployee.getId());
            if (employee == null) {
                repo.create(callEmployee);
            } else {
                repo.update(callEmployee);
            }

            return "Updated " + callEmployee.getId() + " successfully";
        } catch (NullPointerException e) {
            throw new BadRequestException("ID not Found");
        }
    }

    private boolean isValidToken(String tokenValue) {
        return this.tokenList.getTokenList().contains(tokenValue);
    }
}
