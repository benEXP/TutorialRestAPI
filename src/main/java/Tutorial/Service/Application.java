package Tutorial.Service;

import Tutorial.FileRepo.Repo;
import Tutorial.Model.TokenList;
import Tutorial.MongoRepo.MongoRepo;
import com.mongodb.MongoClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Bean
//    public FileRepo repo() throws IOException {
//        return new FileRepo(new File(".\\MockFileRepo.txt"));
//    }

    @Bean
    public Repo repo(){
        return new MongoRepo(mongoClient());
    }

    @Bean
    public TokenList tokenList() {
        return new TokenList();
    }

    @Bean
    public MongoClient mongoClient() {
        return new MongoClient("localhost", 27017);
    }

}