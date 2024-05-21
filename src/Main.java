import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        // input
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter input file path: ");
        String file = scanner.nextLine();

        System.out.println("You entered: " + file);
        scanner = new Scanner(Paths.get(file), StandardCharsets.UTF_8.name());

        String input = scanner.useDelimiter("\\A").next();
        scanner.close();

        Controller controller = new Controller(input);
        List<Token> result = controller.scan();
        Parser parser = new Parser(controller);

        //output vctok
        File vctokPath = new File("C:\\Users\\HELLO\\Downloads\\OutputResult.vctok");
        FileWriter writer = new FileWriter(vctokPath);
        for (Token token : result) {
            writer.write(token.getType().toString() + " " + token.getValue() + "\n");
        }

        // output vcps
        try {
            ASTNode ast = parser.parseProgram();
            System.out.println(ast.toString());
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        writer.flush();

        writer.close();

        System.out.println("File Scanned Completely");
    }

}
