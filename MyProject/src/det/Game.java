package det;

import java.sql.*;
import java.util.*;
import java.time.*;

public class Game {

    private static final String link = "jdbc:mysql://localhost:3306/guessing_game";
    private static final String user = "root";
    private static final String pass = "@Apple2023";

    public static void main(String[] args) throws SQLException  {
        Scanner scanner = new Scanner(System.in);
        Connection connection = null;
        
        try {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("MySQL JDBC Driver registered successfully.");
            } catch (ClassNotFoundException e) {
                System.out.println("Error registering MySQL JDBC Driver: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            // Establish the database connection
            connection = getDatabaseConnection();
            if (connection == null) {
                System.out.println("Failed to connect to the database. Exiting...");
                return;
            }

            while (true) {
                System.out.println("Enter your name:");
                String name = scanner.nextLine();

                playGame(connection, scanner, name);

                System.out.println("Do you want to start a new game? (yes/no)");
                String choice = scanner.nextLine().trim().toLowerCase();

                if (!choice.equals("yes")) {
                    System.out.println("Thank you for playing!");
                    break;
                }
            }
        } finally {
            closeConnection(connection);
            scanner.close();
        }
    }

    private static Connection getDatabaseConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(link, user, pass);
            System.out.println("Successfully connected to the database.");
        } catch (SQLException e) {
            System.out.println("Error while connecting to the database: " + e.getMessage());
        }
        return connection;
    }

    private static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.out.println("Error while closing the database connection: " + e.getMessage());
            }
        }
    }

    private static void playGame(Connection connection, Scanner scanner, String name) throws SQLException {
        String targetNumber = generateNumber();
        System.out.println("Game Started! Guess the 4-digit number.");

        int moves = 0;
        LocalTime startTime = LocalTime.now();

        while (true) {
            moves++;
            System.out.print("Enter your guess: ");
            String guess = scanner.nextLine();

            if (guess.length() != 4 || !guess.matches("\\d+")) {
                System.out.println("Please enter a valid 4-digit number.");
                continue;
            }

            String feedback = checkGuess(targetNumber, guess);
            System.out.println("Feedback: " + feedback);

            if (feedback.equals("++++")) {
                LocalTime endTime = LocalTime.now();
                double timeTaken = Duration.between(startTime, endTime).toMillis() / 1000.0;

                System.out.println("Congratulations! You guessed the number in " + moves + " moves and " + timeTaken + " seconds.");

                double score = calculateScore(moves, timeTaken);
                saveScore(connection, name, moves, timeTaken, score);

                displayBestScore(connection);
                break;
            }
        }
    }

    private static String generateNumber() throws SQLException {
        Random random = new Random();
        Set<Integer> digits = new LinkedHashSet<>();
        while (digits.size() < 4) {
            digits.add(random.nextInt(10));
        }
        StringBuilder number = new StringBuilder();
        for (int digit : digits) {
            number.append(digit);
        }
        return number.toString();
    }

    private static String checkGuess(String target, String guess) {
        StringBuilder feedback = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (guess.charAt(i) == target.charAt(i)) {
                feedback.append("+");
            } else if (target.indexOf(guess.charAt(i)) != -1) {
                feedback.append("-");
            }
        }
        return feedback.toString();
    }

    private static double calculateScore(int moves, double timeTaken) {
        return 1.0 / (moves + timeTaken / 10.0);
    }

    private static void saveScore(Connection connection, String name, int moves, double timeTaken, double score) throws SQLException {
        String query = "INSERT INTO scores (name, guesses, time_taken, score) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setInt(2, moves);
            statement.setDouble(3, timeTaken);
            statement.setDouble(4, score);
            statement.executeUpdate();
        }
    }
    private static void displayBestScore(Connection connection) throws SQLException {
        String query = "SELECT name, guesses, time_taken, score FROM scores ORDER BY score DESC LIMIT 1";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                System.out.println("Best Score:");
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("Moves: " + resultSet.getInt("guesses"));
                System.out.println("Time Taken: " + resultSet.getDouble("time_taken") + " seconds");
                System.out.println("Score: " + resultSet.getDouble("score"));
            }
        }
    }
}
