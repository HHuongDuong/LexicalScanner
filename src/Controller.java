import java.util.ArrayList;
import java.util.List;

public class Controller {
    private Automaton automaton;
    private List<Token> result;
    private int CurPos;
    private String input;

    public Controller(String input) {
        this.result = new ArrayList<>();
        this.automaton = new Automaton();
        this.CurPos = 0;
        this.input = input;
    }
    // analyze a string and add to result if valid
    public void analyze(String input) {
        StringBuilder lexemeBuilder = new StringBuilder();
        State.STATE currentState = State.STATE.s0;
        for (char c : input.toCharArray()) {
            State.STATE nextState = automaton.exercuteTransition(currentState, c);
            if (nextState != State.STATE.InvalidState) {
                currentState = nextState;
                lexemeBuilder.append(c);
            } else {
                // Invalid transition encountered
                break;
            }
        }

        State.STATE finalState = currentState;
        State.STATE initialState = State.STATE.s0;
        String lexeme = lexemeBuilder.toString();

        // Check if the final state is valid
        if (automaton.finalStates.containsKey(finalState)) {
            if (LexicalScanner.isKeyword(lexeme)) {
                //check keyword
                result.add(new Token(LexicalScanner.Type.Keyword, lexeme));
            } else {
                // Get the corresponding token type
                LexicalScanner.Type tokenType = automaton.finalStates.get(finalState);
                result.add(new Token(tokenType, lexeme));
            }
        } else {
            // If the final state is not valid, return an invalid token
            result.add(new Token(LexicalScanner.Type.Invalid, lexeme));
        }
    }
    // scan input
    public List<Token> scan() {
        System.out.println("Scanning...");
        while (CurPos < input.length()) {
            char CurChar = input.charAt(CurPos);
            if (LexicalScanner.isWhitespace(CurChar)) {
                CurPos++;
            }  else if (CurChar == '/') {
                if (peekNextChar() == '/') {
                    skipSingleLineComment();
                } else if (peekNextChar() == '*') {
                    skipMultiLineComment();
                }   else scanOperator();
            }  else if (LexicalScanner.isAlaphabet(CurChar)) {
                scanIdentifierOrKeyWord();
            }   else if (LexicalScanner.isNumber(CurChar) || CurChar == '.') {
                scanLiteral();
            }   else if (CurChar == '"') {
                scanStringLiteral();
            }   else if (LexicalScanner.isSeparator(CurChar)) {
                scanSeparator();
            }   else if (LexicalScanner.isOperator(CurChar)) {
                scanOperator();
            }   else {
                CurPos++;
            }
        }
        return result;
    }
// ...
    public void scanOperator() {
        String CurChar = String.valueOf(input.charAt(CurPos));
        analyze(CurChar);
        CurPos++;
    }

    public void scanSeparator() {
        String CurChar = String.valueOf(input.charAt(CurPos));
        analyze(CurChar);
        CurPos++;
    }

    public void scanStringLiteral() {
        StringBuilder literal = new StringBuilder();
        literal.append(input.charAt(CurPos));
        CurPos++;
        while (CurPos < input.length() && input.charAt(CurPos) != '"') {
            literal.append(input.charAt(CurPos));
            CurPos++;
        }
        if (CurPos < input.length() && input.charAt(CurPos) == '"') {
            literal.append(input.charAt(CurPos));
            CurPos++;
            analyze(literal.toString());
        } else {
            System.out.println("Error: Missing closing double quote");
        }
    }

    public void scanLiteral() {
        StringBuilder literal = new StringBuilder();
        while (CurPos < input.length() && (LexicalScanner.isNumber(input.charAt(CurPos)) || input.charAt(CurPos) == '.' || input.charAt(CurPos) == 'E')) {
            literal.append(input.charAt(CurPos));
            CurPos++;
        }
        analyze(literal.toString());
    }

    public void scanIdentifierOrKeyWord() {
        StringBuilder identifier = new StringBuilder();
        while (CurPos < input.length() && (LexicalScanner.isAlaphabet(input.charAt(CurPos)) || LexicalScanner.isNumber(input.charAt(CurPos)))) {
            identifier.append(input.charAt(CurPos));
            CurPos++;
        }
        analyze(identifier.toString());
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
