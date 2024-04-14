public class LexicalScanner {
    public static final String[] KEYWORD = {"boolean", "break", "continue", "else", "for", "float", "if", "int", "return", "void", "while"};
    public static final String OPERATOR = "&|+-*/%=<>!:";
    public static final String SEPARATOR = "[]{}().,;";
    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz_" +
            "QWERTYUIOPASDFGHJKLZXCVBNM";
    public static final String NUMBER = "0123456789";

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