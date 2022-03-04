package bookselfdataloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import connection.DataStaxAstraProperties;


/**
 * Main application class with main method that runs the Spring Boot app
 */

@SpringBootApplication
@ComponentScan(basePackages = {"Author.AuthorRepository"})
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BookselfDataLoaderApplication {

    @Autowired 
    AuthorRepository repository;

    @Value("${datadump.location.author}")
    private String authorDumpLocation;

    @Value("${datadump.location.work}")
    private String workDumpLocation;

    private void initAuthors() throws IOException{
        Path path = Paths.get(authorDumpLocation);
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                String jsonString  = line.substring(line.indexOf("{"));
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Author author = new Author();
                    author.setName(jsonObject.optString("name"));
                    author.setPersonalName(jsonObject.optString("personal_name"));
                    author.setId(jsonObject.optString("key").replace("/authors/", ""));

                    repository.save(author);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
            });
        }
        

    }
    private void initWorks(){

    }

    @PostConstruct
    public void start() throws IOException{
        initAuthors();
        initWorks();

    }

    /**
     *Spring Boot app use the Astra secure bundle to connect to the database
     */
	@Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }

    public static void main(String[] args) {
		SpringApplication.run(BookselfDataLoaderApplication.class, args);
	}




}