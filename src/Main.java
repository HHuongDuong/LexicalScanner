import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
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
//        ASTNode root = parser.program();
//        String formattedOutput = formatAST(root);
//        System.out.println(formattedOutput);

        writer.flush();

        writer.close();

        System.out.println("File Scanned Completely");
    }

    // Method to format AST with brackets
    private static String formatAST(ASTNode node) {
        return formatNode(node).replaceAll(" \\(", "(").replaceAll("\\) ", ")");
    }

    // Recursive method to format nodes with brackets
    private static String formatNode(ASTNode node) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(node.value);
        for (ASTNode child : node.children) {
            builder.append(" ").append(formatNode(child));
        }
        builder.append(")");
        return builder.toString();
    }
}
