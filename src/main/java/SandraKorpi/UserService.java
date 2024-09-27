package SandraKorpi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser; // Importera JsonParser
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
import java.util.Map;

public class UserService {
    private static final String API_BASE_URL = "http://NYbookapi-env.eba-i4k2gzqx.eu-north-1.elasticbeanstalk.com"; // Ändra till din API-url
    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private Gson gson = new Gson(); // Använd Gson direkt istället för ObjectMapper

    private String jwtToken; // Lagra token i en privat variabel

    public void registerUser(String username, String email, String password) throws IOException {
        String registerUrl = API_BASE_URL + "/auth/register"; // Använd den korrekta registrerings-endpoint
        HttpPost postRequest = new HttpPost(registerUrl);

        Map<String, String> user = new HashMap<>();
        user.put("userName", username);
        user.put("email", email);
        user.put("password", password);

        String json = gson.toJson(user); // Använd Gson för att konvertera till JSON
        postRequest.setEntity(new StringEntity(json, org.apache.hc.core5.http.ContentType.APPLICATION_JSON));

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

    public String loginUser(String username, String password) throws IOException {
        String loginUrl = API_BASE_URL + "/auth/login";
        HttpPost postRequest = new HttpPost(loginUrl);

        // Skapa LoginDto
        LoginDto loginDto = new LoginDto(username, password);
        StringEntity entity = new StringEntity(gson.toJson(loginDto), org.apache.hc.core5.http.ContentType.APPLICATION_JSON);
        postRequest.setEntity(entity);
        postRequest.setHeader("Content-type", "application/json");

        // Skicka förfrågan och hantera svaret
        try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
            if (response.getCode() == HttpStatus.SC_OK) {
                String responseBody = EntityUtils.toString(response.getEntity());
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                this.jwtToken = jsonResponse.get("token").getAsString(); // Anta att token kommer under "token"
                // Nu kan du använda jwtToken i framtida anrop
            } else {
                System.out.println("Login failed: " + response.getCode());
                System.out.println(EntityUtils.toString(response.getEntity()));

            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return this.jwtToken; // Eller bara returnera ingenting
    }
}




