package SandraKorpi;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static UserService userService = new UserService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("Välj ett alternativ:");
            System.out.println("1. Registrera användare");
            System.out.println("2. Logga in");
            System.out.println("3. Avsluta");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Konsumera den nya raden

            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    logoutUser();
                    System.out.println("Avslutar programmet.");
                    return; // Avsluta programmet
                default:
                    System.out.println("Ogiltigt val. Försök igen.");
            }
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

            String token = userService.loginUser(username, password);
            if (token != null) {
                System.out.println("Inloggning lyckades! JWT-token: " + token);
                // Här kan du lägga till kod för att hantera inloggad status
            } else {
                System.out.println("Inloggning misslyckades.");
            }
        } catch (IOException e) {
            System.out.println("Ett fel uppstod vid inloggning: " + e.getMessage());
        }
    }
    public static void logoutUser() {
        this.jwtToken = null; // Ta bort token för att logga ut användaren
        System.out.println("Användaren har loggats ut.");
    }
}
