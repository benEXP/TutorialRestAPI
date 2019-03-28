package Tutorial.MongoRepo;

import Tutorial.Exceptions.FileRepoExceptions.DuplicateIdException;
import Tutorial.Exceptions.FileRepoExceptions.IdNotFoundException;
import Tutorial.Exceptions.FileRepoExceptions.NoIdException;
import Tutorial.FileRepo.Repo;
import Tutorial.Model.Employee;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mongodb.client.model.Filters.eq;
import java.util.HashMap;
import java.util.Map;

public class MongoRepo implements Repo {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection mongoCollection;
    private Map<Integer, Employee> employees;

    @Autowired
    public MongoRepo(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        this.mongoDatabase = this.mongoClient.getDatabase("local");
        this.mongoCollection = this.mongoDatabase.getCollection("collection");
    }

    @Override
    public void create(Employee employee) {
        if (employee.getId() == 0) {
            throw new NoIdException("ID cannot be 0/empty");
        }

        if(isEmployeeExist(employee.getId())) {
            throw new DuplicateIdException("Same ID Existed");
        }

        Document document = new Document();
        document.put("id", employee.getId());
        document.put("age", employee.getAge());
        document.put("role", employee.getRole());
        document.put("name", employee.getName());

        mongoCollection.insertOne(document);
    }

    @Override
    public Employee getByID(int id) {
        if(!isEmployeeExist(id)) {
            throw new IdNotFoundException("no such ID");
        }
        Employee returnEmployee = new Employee();
        FindIterable<Document> getEmployeeDocs = mongoCollection.find(eq("id", id));

        for (Document doc : getEmployeeDocs) {
            returnEmployee.setId(doc.getInteger("id"));
            returnEmployee.setAge(doc.getInteger("age"));
            returnEmployee.setName(doc.getString("name"));
            returnEmployee.setRole(doc.getString("role"));
        }

        return returnEmployee;
    }

    @Override
    public Map<Integer, Employee> getAll() {
        Map<Integer, Employee> allEmployeeMap= new HashMap<>();
        FindIterable<Document> allEmployeeDocs = mongoCollection.find();

        for(Document doc : allEmployeeDocs) {
            Employee iterateEmployee = new Employee();
            iterateEmployee.setId(doc.getInteger("id"));
            iterateEmployee.setAge(doc.getInteger("age"));
            iterateEmployee.setName(doc.getString("name"));
            iterateEmployee.setRole(doc.getString("role"));

            allEmployeeMap.put(iterateEmployee.getId(), iterateEmployee);
        }

        return allEmployeeMap;
    }

    @Override
    public void update(Employee employee) {
        if(!isEmployeeExist(employee.getId())) {
            throw new NullPointerException("No such ID");
        }
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("id", employee.getId());
        newDocument.put("age", employee.getAge());
        newDocument.put("role", employee.getRole());
        newDocument.put("name", employee.getName());

        BasicDBObject searchQuery = new BasicDBObject().append("id", employee.getId());

        mongoCollection.replaceOne(searchQuery, newDocument);
    }

    @Override
    public void deleteByID(int id) {
        if(!isEmployeeExist(id)) {
            throw new IdNotFoundException("ID not found");
        }
       BasicDBObject query = new BasicDBObject();
       query.append("id", id);

       mongoCollection.findOneAndDelete(query);
    }

    public int count() {
        Long l = mongoCollection.count();
        return l.intValue();
    }

    private boolean isEmployeeExist(int id){
        this.employees = getAll();
        return this.employees.containsKey(id);
    }
}
