import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Controller {
    public Controller(String fileName) {
        List<Token> result = new ArrayList<Token>();
        Scanner scanner = null;
        int lineNo = 0;

        try {
            scanner = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNextLine()) {
            lineNo++;
            String line = scanner.nextLine();
            String[] lineparts = line.split("\\s+");
            for (String str: lineparts) {

            }
        }
    }

}
