import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String filePath = scanner.next();
        Controller controller = new Controller(filePath);
        List<Token> results = controller.scan();
        for (Token token : results) {
            System.out.println(token);
        }
    }
}
