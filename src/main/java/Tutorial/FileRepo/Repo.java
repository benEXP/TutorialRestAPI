package Tutorial.FileRepo;

import Tutorial.Exceptions.FileRepoExceptions.IdNotFoundException;
import Tutorial.Exceptions.FileRepoExceptions.NoIdException;
import Tutorial.Model.Employee;

import java.util.Map;

public interface Repo {
    void create(Employee employee) throws NullPointerException, NoIdException;

    Employee getByID(int id) throws IdNotFoundException;

    Map<Integer, Employee> getAll();

    void update(Employee employee) throws IdNotFoundException;

    void deleteByID(int id);
}
