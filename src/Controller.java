import java.util.ArrayList;
import java.util.List;


public class Controller {
    public String input;
    public List<Token> result;
    public int CurPos;

    public Controller(String input) {

        this.input = input;
        this.result = new ArrayList<>();
        this.CurPos = 0;
    }


    public List<Token> scan() {
        System.out.println("Scanning...");
        while (CurPos < input.length()) {
            char CurChar = input.charAt(CurPos);
            if (LexicalScanner.isWhitespace(CurChar)) {
                CurPos++;
            } else if (LexicalScanner.isAlaphabet(CurChar)) {
                scanIdentifierOrKeyword();
            } else if (LexicalScanner.isNumber(CurChar) || CurChar == '.') {
                scanLiteral();
            } else if (CurChar == '"') {
                scanStringLiteral();
            } else if (CurChar == '/') {
                if (peekNextChar() == '/') {
                    skipSingleLineComment();
                } else if (peekNextChar() == '*') {
                    skipMultiLineComment();
                } else {
                    scanOperator();
                }
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
        while (CurPos < input.length() && LexicalScanner.isAlaphabet(input.charAt(CurPos))) {
            identifier.append(input.charAt(CurPos));
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
        while (CurPos < input.length() && (LexicalScanner.isNumber(input.charAt(CurPos)) || input.charAt(CurPos) == '.')) {
            if (input.charAt(CurPos) == '.') {
                temp = true;
            }
            literal.append(input.charAt(CurPos));
            CurPos++;
        }
        if (temp) {
            result.add(new Token(LexicalScanner.Type.RealLiteral, literal.toString()));
        } else result.add(new Token(LexicalScanner.Type.IntLiteral, literal.toString()));
    }

    public void scanStringLiteral() {
        StringBuilder literal = new StringBuilder();
        while (CurPos < input.length() && input.charAt(CurPos) != '"') {
            literal.append(input.charAt(CurPos));
            CurPos++;
        }
        if (CurPos < input.length() && input.charAt(CurPos) == '"') {
            CurPos++;
            result.add(new Token(LexicalScanner.Type.StrLiteral, literal.toString()));
        } else {
            System.out.println("Error: Missing closing double quote");
        }
    }

    public void scanOperator() {
        String CurChar = String.valueOf(input.charAt(CurPos));
        result.add(new Token(LexicalScanner.Type.Operator, CurChar));
        CurPos++;
    }

    public void scanSeparator() {
        String CurChar = String.valueOf(input.charAt(CurPos));
        result.add(new Token(LexicalScanner.Type.Separator, CurChar));
        CurPos++;
    }

    public char peekNextChar() {
        if (CurPos + 1 < input.length()) {
            return input.charAt(CurPos + 1);
        } else {
            return '\0'; // Null character if end of string
        }
    }

    public void skipSingleLineComment() {
        while (CurPos < input.length() && input.charAt(CurPos) != '\n') {
            CurPos++;
        }
    }

    public void skipMultiLineComment() {
        CurPos += 2; // Skip '/*'
        while (CurPos < input.length() - 1 && !(input.charAt(CurPos) == '*' && input.charAt(CurPos + 1) == '/')) {
            CurPos++;
        }
        CurPos += 2; // Skip '*/'
    }
}

