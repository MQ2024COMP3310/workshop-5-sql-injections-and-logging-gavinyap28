package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    static {
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            System.out.println("Wordle created and connected.");
        } else {
            System.out.println("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            System.out.println("Wordle structures in place.");
        } else {
            System.out.println("Not able to launch. Sorry!");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if (line.matches("^[a-z]{4}$")) {
                    logger.info("Valid word added: " + line);
                    wordleDatabaseConnection.addValidWord(i, line);
                } else {
                    logger.severe("Invalid word in data.txt: " + line);
                }
                i++;
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Not able to load words from file.", e);
            System.out.println("Not able to load. Sorry!");
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            String guess = "";
            while (true) {
                System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                guess = scanner.nextLine();

                if (guess.equals("q")) {
                    break;
                }

                if (!guess.matches("^[a-z]{4}$")) {
                    logger.log(Level.WARNING, "Invalid guess: {0}", guess);
                    System.out.println("Input is not a valid 4-letter word. Please try again.");
                    continue;
                }

                if (wordleDatabaseConnection.isValidWord(guess)) {
                    System.out.println("Success! It is in the list.\n");
                } else {
                    System.out.println("Sorry. This word is NOT in the list.\n");
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.SEVERE, "Scanner error.", e);
        }
    }
}
