public class LexicalScanner {
    public static final String[] KEYWORD = {"boolean", "break", "continue", "else", "for", "float", "if", "int", "return", "void", "while"};
    public static final String OPERATOR = "&|+-*/%=<>!:";
    public static final String SEPARATOR = "[]{}().,;";
    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz_" +
            "QWERTYUIOPASDFGHJKLZXCVBNM";
    public static final String NUMBER = "0123456789";

    // Check if the character is whitespace (space, tab, newline, carriage return).
    public static boolean isWhitespace(char token) {
        return Character.isWhitespace(token);
    }
    // Check if character is alphabet
    public static boolean isAlaphabet(char token){return ALPHABET.contains(String.valueOf(token));}
    // number
    public static boolean isNumber(char token){return NUMBER.contains(String.valueOf(token));}
    //keyword
    public static boolean isKeyword(String token){
        for(int i=0;i<KEYWORD.length;i++){
            if (KEYWORD[i].equals(token))
                return true;
        }
        return false;
    }
    //sep
    public static boolean isSeparator(char token) {
        return SEPARATOR.contains(String.valueOf(token));
    }
    //ope
    public static boolean isOperator(char token){
        return OPERATOR.contains(String.valueOf(token));
    }


    public enum Type {
        Identifier, Keyword, Operator, Separator, IntLiteral, RealLiteral, StrLiteral, Invalid, EOF
    }
}