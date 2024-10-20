package SandraKorpi;

import com.fasterxml.jackson.databind.ObjectMapper; // Importera ObjectMapper
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private static final String API_BASE_URL = "http://NYbookapi-env.eba-i4k2gzqx.eu-north-1.elasticbeanstalk.com"; // Ändra till din API-url
    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private ObjectMapper objectMapper = new ObjectMapper();

    private String jwtToken;

    // Registrera användare
    public void registerUser(String username, String email, String password) throws IOException {
        String registerUrl = API_BASE_URL + "/auth/register";
        HttpPost postRequest = new HttpPost(registerUrl);

        // Skapa användardata som ska skickas
        Map<String, String> user = new HashMap<>();
        user.put("userName", username);
        user.put("email", email);
        user.put("password", password);

        // Konvertera objekt till JSON med ObjectMapper
        String json = objectMapper.writeValueAsString(user);
        postRequest.setEntity(new StringEntity(json, org.apache.hc.core5.http.ContentType.APPLICATION_JSON));

        // Skicka förfrågan
        try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
            int statusCode = response.getCode();
            if (statusCode == 201) {
                System.out.println("User registered successfully!");
            } else {
                System.out.println("Failed to register user: " + statusCode);
                System.out.println(EntityUtils.toString(response.getEntity()));
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    // Logga in användare
    public void loginUser(String username, String password) throws IOException, ParseException {
        String loginUrl = API_BASE_URL + "/auth/login";
        HttpPost postRequest = new HttpPost(loginUrl);

        // Skapa LoginDto
        LoginDto loginDto = new LoginDto(username, password);

        // Konvertera LoginDto till JSON med ObjectMapper
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(loginDto), org.apache.hc.core5.http.ContentType.APPLICATION_JSON);
        postRequest.setEntity(entity);
        postRequest.setHeader("Content-type", "application/json");

        // Skicka förfrågan och hantera svaret
        try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
            if (response.getCode() == HttpStatus.SC_OK) {
                String responseBody = EntityUtils.toString(response.getEntity());

                // Konvertera svaret till Map för att hämta JWT-token
                Map<String, String> jsonResponse = objectMapper.readValue(responseBody, Map.class);
                this.jwtToken = jsonResponse.get("token"); // Anta att token kommer under "token"
            } else {
                // Om inloggning misslyckas, sätt jwtToken till null
                this.jwtToken = null;
            }
        }
    }

    public void addBook(BookDto bookDto) throws IOException, ParseException {
        String addBookUrl = API_BASE_URL + "/books/add";
        HttpPost postRequest = new HttpPost(addBookUrl);
        postRequest.setHeader("Authorization", "Bearer " + jwtToken); // Lägg till JWT-token i headern

        // Konvertera BookDto till JSON med ObjectMapper
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(bookDto), org.apache.hc.core5.http.ContentType.APPLICATION_JSON);
        postRequest.setEntity(entity);
        postRequest.setHeader("Content-type", "application/json");

        // Skicka förfrågan och hantera svaret
        try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
            if (response.getCode() == HttpStatus.SC_CREATED || response.getCode() == HttpStatus.SC_OK) {
                System.out.println("Boken har lagts till i databasen.");
            } else {
                System.out.println("Misslyckades med att lägga till boken: " + response.getCode());
                System.out.println(EntityUtils.toString(response.getEntity()));
            }
        }
    }

    public void showAllBooks() {
        String addBookUrl = API_BASE_URL + "/books"; // för att hämta alla books
        HttpGet getRequest = new HttpGet(addBookUrl);
        getRequest.setHeader("Authorization", "Bearer " + jwtToken); // Lägg till JWT-token i headern
        try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
            int statusCode = response.getCode();
            if (statusCode == 200) {
                // Läs svaret från API:t
                String jsonResponse = EntityUtils.toString(response.getEntity());
                // Använd ObjectMapper för att konvertera JSON till en lista av books
                List<BookDto> bookList = objectMapper.readValue(jsonResponse,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class));
                // Skriver ut böckerna
                for( BookDto book : bookList) {
                    System.out.println("Titel: " + book.getTitle() + " författare: " + book.getAuthor());
                }
            } else {
                System.out.println("Hittade inte några böcker: " + statusCode);
                System.out.println(EntityUtils.toString(response.getEntity()));
            }

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void getFavoriteBooks(){
        String addBookUrl = API_BASE_URL + "/users/favorite-books";
        HttpGet getRequest = new HttpGet(addBookUrl);
        getRequest.setHeader("Authorization", "Bearer " + jwtToken); // Lägg till JWT-token i headern
        try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
            int statusCode = response.getCode();
            if (statusCode == 200) {
                // Läs svaret från API:t
                String jsonResponse = EntityUtils.toString(response.getEntity());
                // Använd ObjectMapper för att konvertera JSON till en lista av books
                List<BookDto> bookList = objectMapper.readValue(jsonResponse,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class));
                // Skriv ut böckerna i konsolen
                for( BookDto book : bookList) {
                    System.out.println("Titel: " + book.getTitle() + " författare: " + book.getAuthor());
                }
            } else if (statusCode == 204) {
                System.out.println("Du har inga favoritböcker ännu!");

            } else {
                System.out.println("Något har gått snett, felkod:  " + statusCode);
                System.out.println(EntityUtils.toString(response.getEntity()));
            }

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Logga ut användare
    public void logoutUser() {
        this.jwtToken = null; // Ta bort token för att logga ut användaren
        System.out.println("Användaren har loggats ut.");
    }

    public String getJwtToken() {
        return this.jwtToken;
    }
}
