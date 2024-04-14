import sun.misc.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Controller {
    public String input;
    public List<Token> result;
    public int CurPos;
    String CurChar = String.valueOf(input.charAt(CurPos));

    public Controller(String FilePath) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(FilePath)).useDelimiter("\\A");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String input = scanner.hasNext() ? scanner.next() : "";

        this.input = input;
        this.result = new ArrayList<>();
        this.CurPos = 0;
    }

    public List<Token> scan() {
        while (CurPos < input.length()) {
            if (LexicalScanner.isWhitespace(CurChar)) {
                CurPos++;
            } else if (LexicalScanner.isAlaphabet(CurChar)) {
                scanIdentifierOrKeyword();
            } else if (LexicalScanner.isNumber(CurChar) || CurChar == ".") {
                scanLiteral();
            } else if (CurChar == "\"") {
                scanStringLiteral();
            } else if (LexicalScanner.isSeparator(CurChar)) {
                scanSeparator();
            } else if (LexicalScanner.isOperator(CurChar)) {
                scanOperator();
            } else {
                CurPos++;
            }
        }
        return result;
    }

    public void scanIdentifierOrKeyword() {
        StringBuilder identifier = new StringBuilder();
        while (CurPos < input.length() && LexicalScanner.isAlaphabet(CurChar)) {
            identifier.append(CurChar);
            CurPos++;
        }
        String identifierStr = identifier.toString();
        if (LexicalScanner.isKeyword(identifierStr)) {
            result.add(new Token(LexicalScanner.Type.Keyword, identifierStr));
        } else {
            result.add(new Token(LexicalScanner.Type.Identifier, identifierStr));
        }
    }

    public void scanLiteral() {
        boolean temp = false;
        StringBuilder literal = new StringBuilder();
        while (CurPos < input.length() && (LexicalScanner.isNumber(CurChar) || CurChar == ".")) {
            if (CurChar == ".") {
                temp = true;
            }
            literal.append(CurChar);
            CurPos++;
        }
        if (temp) {
            result.add(new Token(LexicalScanner.Type.RealLiteral, literal.toString()));
        } else result.add(new Token(LexicalScanner.Type.IntLiteral, literal.toString()));
    }

    public void scanStringLiteral() {
        StringBuilder literal = new StringBuilder();
        while (CurPos < input.length() && CurChar != "\"") {
            literal.append(CurChar);
            CurPos++;
        }
        if (CurPos < input.length() && CurChar == "\"") {
            CurPos++;
            result.add(new Token(LexicalScanner.Type.StrLiteral, literal.toString()));
        }
    }

    public void scanOperator() {
        result.add(new Token(LexicalScanner.Type.Operator, CurChar));
    }

    public void scanSeparator() {
        result.add(new Token(LexicalScanner.Type.Separator, CurChar));
    }

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

