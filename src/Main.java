import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter input file path: ");
        String file = scanner.nextLine();
        System.out.println("You entered: " + file);
        String input = convert(file);
        Controller controller = new Controller(input);
        List<Token> results = controller.scan();
        for (Token token : results) {
            System.out.println(token);
        }
    }

    private static String convert(String filePath) {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader buffer = new BufferedReader(
                new FileReader(filePath))) {

            String str;

            while ((str = buffer.readLine()) != null) {
                builder.append(str).append("\n");
            }
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }
}

