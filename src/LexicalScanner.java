public class LexicalScanner {
    public static final String[] KEYWORD = {"boolean ", "break", "continue", "else", "for", "float", "if", "int", "return", "void", "while"};
    public static final String OPERATOR = "& | + - * / % = < > ! : ";
    public static final String SEPARATOR = "[ ] { } ( ) . , ; ";
    public static final String ALPHABET = "a b c d e f g h i j k l m n o p q r s t u v w x y z _ " +
            "Q W E R T Y U I O P A S D F G H J K L Z X C V B N M";
    public static final String NUMBER = " 0 1 2 3 4 5 6 7 8 9";
    public static boolean isWhitespace(char token) {
        // Check if the character is whitespace (space, tab, newline, carriage return).
        return Character.isWhitespace(token);
    }
    public static boolean isAlaphabet(char token){return ALPHABET.contains(String.valueOf(token));}
    public static boolean isNumber(char token){return NUMBER.contains(String.valueOf(token));}
    public static boolean isKeyword(String token){
        for(int i=0;i<KEYWORD.length;i++){
            if (KEYWORD[i].equals(token))
                return true;
        }
        return false;
    }
    public static boolean isSeparator(char token) {
        return SEPARATOR.contains(String.valueOf(token));
    }
    public static boolean isOperator(char token){
        return OPERATOR.contains(String.valueOf(token));
    }

    public static enum Type {
        Identifier, Keyword, Operator, Separator, IntLiteral, RealLiteral, StrLiteral;
    }
}