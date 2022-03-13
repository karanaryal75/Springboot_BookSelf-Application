/**
 * Used for loading data to cassandra database
 * Once done loading the data, no longer need of this code
 * Kept here just for reference.
 */

// package bookselfdataloader;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;

// import javax.annotation.PostConstruct;

// import org.json.JSONArray;
// import org.json.JSONException;
// import org.json.JSONObject;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
// import org.springframework.boot.context.properties.EnableConfigurationProperties;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.ComponentScan;
// import connection.DataStaxAstraProperties;


// /**
//  * Main application class with main method that runs the Spring Boot app
//  */

// @SpringBootApplication
// @ComponentScan(basePackages = {"Author.AuthorRepository"})
// @EnableConfigurationProperties(DataStaxAstraProperties.class)
// public class BookselfDataLoaderApplication {

//     @Autowired 
//     AuthorRepository repository;

//     @Autowired
//     BookRepository bookRepository;

//     @Value("${datadump.location.author}")
//     private String authorDumpLocation;

//     @Value("${datadump.location.work}")
//     private String workDumpLocation;

//     // private void initAuthors() throws IOException{
//     //     Path path = Paths.get(authorDumpLocation);
//     //     try (Stream<String> lines = Files.lines(path)) {
//     //         lines.forEach(line -> {
//     //             String jsonString  = line.substring(line.indexOf("{"));
//     //             try {
//     //                 JSONObject jsonObject = new JSONObject(jsonString);
//     //                 Author author = new Author();
//     //                 author.setName(jsonObject.optString("name"));
//     //                 author.setPersonalName(jsonObject.optString("personal_name"));
//     //                 author.setId(jsonObject.optString("key").replace("/authors/", ""));
//     //                 repository.save(author);
//     //             } catch (JSONException e) {
//     //                 e.printStackTrace();
//     //             }
                
//     //         });
//     //     }
        

//     // }

//     private void initWorks() throws IOException{
//         Path path = Paths.get(workDumpLocation);
//         DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
//         try (Stream<String> lines = Files.lines(path)){
//             lines.forEach(line -> {
//                 String jString = line.substring(line.indexOf("{"));
                
//                 try {
//                     JSONObject jsonObject = new JSONObject(jString);
//                     Book book = new Book();

//                     book.setId(jsonObject.getString("key").replace("/works/", ""));

//                     book.setName(jsonObject.optString("title"));

//                     JSONObject desJsonObj = jsonObject.optJSONObject("description");
//                     if (desJsonObj != null) book.setDescription(desJsonObj.optString("value"));

//                     JSONObject publishedObj = jsonObject.optJSONObject("created");
//                     if (publishedObj != null) {
//                         String dateString = publishedObj.getString("value");
//                         book.setPublishDate(LocalDate.parse(dateString,dateFormat));
//                     }

//                     JSONArray coverJsonArray = jsonObject.optJSONArray("covers");
//                     if (coverJsonArray != null){
//                         List<String> cover_ids = new ArrayList<>();
//                         for (int i = 0; i<coverJsonArray.length(); i++) {
//                             cover_ids.add(coverJsonArray.getString(i));
//                         }
//                         book.setCover_ids(cover_ids);
//                     }

//                     JSONArray authorJsonArray = jsonObject.optJSONArray("authors");
//                     if (authorJsonArray != null){
//                         List<String> author_list_id = new ArrayList<>();
//                         for (int i =0; i<authorJsonArray.length(); i++){
//                             String authorId = authorJsonArray.getJSONObject(i).getJSONObject("author").
//                             getString("key").replace("/authors/", "");
//                             author_list_id.add(authorId);
//                         }
//                         book.setAuthorId(author_list_id);
//                         List<String> authorNames = author_list_id.stream().map(id -> repository.findById(id))
//                                     .map(optionalAuthor -> {
//                                         if (!optionalAuthor.isPresent()) return "Unknown Author";
//                                         return optionalAuthor.get().getName();
//                                     }).collect(Collectors.toList());
//                         book.setAuthorNames(authorNames);
//                     }
//                     bookRepository.save(book);
                    

//                 } catch (JSONException e) {
//                     e.printStackTrace();
//                 }
//             }); 
//         }
//     }

//     @PostConstruct
//     public void start() throws IOException{
//         //initAuthors();
//         initWorks();

//     }

//     /**
//      *Spring Boot app use the Astra secure bundle to connect to the database
//      */
// 	@Bean
//     public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
//         Path bundle = astraProperties.getSecureConnectBundle().toPath();
//         return builder -> builder.withCloudSecureConnectBundle(bundle);
//     }

//     public static void main(String[] args) {
// 		SpringApplication.run(BookselfDataLoaderApplication.class, args);
// 	}




// }