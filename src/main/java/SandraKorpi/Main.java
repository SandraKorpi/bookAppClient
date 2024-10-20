package SandraKorpi;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static UserService userService = new UserService();
    private static Scanner scanner = new Scanner(System.in);

    private static boolean isLoggedIn = false; // för att kontrollera om användaren är inloggad

    public static void main(String[] args) throws IOException, ParseException {
        while (true) {
            System.out.println("Välj ett alternativ:");
            System.out.println("1. Registrera användare");
            System.out.println("2. Logga in");
            if (isLoggedIn) { // Visa alternativet för att lägga till bok endast när man är inloggad
                System.out.println("4. Lägg till bok");
                System.out.println("5. Visa alla böcker");
                System.out.println("6.Visa dina favoritböcker");
            }
            System.out.println("3. Avsluta");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    userService.logoutUser();
                    System.out.println("Avslutar programmet.");
                    return; // Avsluta programmet
                case 4:
                    addBook();
                    break;
                case 5:
                    userService.showAllBooks();
                    break;
                case 6: userService.getFavoriteBooks();
                break;

                default:
                    System.out.println("Ogiltigt val. Försök igen.");
            }
        }
    }
    private static void addBook() {
        try {
            System.out.print("Ange boktitel: ");
            String title = scanner.nextLine();
            System.out.print("Ange författare: ");
            String author = scanner.nextLine();
            System.out.print("Ange publiceringsår: ");
            int yearPublished = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Ange genre: ");
            String genre = scanner.nextLine();
            System.out.print("Rekommenderad (true/false): ");
            boolean isRecommended = scanner.nextBoolean();
            scanner.nextLine();

            BookDto bookDto = new BookDto(0, title, author, yearPublished, genre, isRecommended);

            userService.addBook(bookDto);
        } catch (IOException | ParseException e) {
            System.out.println("Ett fel uppstod vid tillägg av boken: " + e.getMessage());
        }
    }


    private static void registerUser() {
        try {
            System.out.print("Ange användarnamn: ");
            String username = scanner.nextLine();
            System.out.print("Ange e-post: ");
            String email = scanner.nextLine();
            System.out.print("Ange lösenord: ");
            String password = scanner.nextLine();

            userService.registerUser(username, email, password);
        } catch (IOException e) {
            System.out.println("Ett fel uppstod vid registrering: " + e.getMessage());
        }
    }

    private static void loginUser() {
        try {
            System.out.print("Ange användarnamn: ");
            String username = scanner.nextLine();
            System.out.print("Ange lösenord: ");
            String password = scanner.nextLine();

            userService.loginUser(username, password);

            // Kontrollera om inloggning lyckades baserat på om jwtToken inte är null
            if (userService.getJwtToken() != null) {
                System.out.println("Inloggning lyckades!");

                isLoggedIn = true;
            } else {
                System.out.println("Inloggning misslyckades.");

            }
        } catch (IOException e) {
            System.out.println("Ett fel uppstod vid inloggning: " + e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


}
