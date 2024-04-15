import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter input file path: ");
        String file = scanner.nextLine();
        System.out.println("You entered: " + file);
        scanner = new Scanner(Paths.get(file), StandardCharsets.UTF_8.name());
        String input = scanner.useDelimiter("\\A").next();
        scanner.close();
        Controller controller = new Controller(input);
        List<Token> result = controller.scan();
        for (Token token : result) {
            System.out.println(token.getType() + " " + token.getValue() );
        }
    }


/*    private static String convert(String filePath) {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader buffer = new BufferedReader(new FileReader(filePath))) {

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
    }*/
}

