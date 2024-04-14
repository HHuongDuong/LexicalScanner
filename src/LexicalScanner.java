public class LexicalScanner {
    public static final String[] KEYWORD = {"boolean ", "break", "continue", "else", "for", "float", "if", "int", "return", "void", "while"};
    public static final String OPERATOR = "& | + - * / % = < > ! : ";
    public static final String SEPARATOR = "[ ] { } ( ) . , ; ";
    public static final String ALPHABET = "a b c d e f g h i j k l m n o p q r s t u v w x y z _ " +
            "Q W E R T Y U I O P A S D F G H J K L Z X C V B N M";
    public static final String NUMBER = " 0 1 2 3 4 5 6 7 8 9";
    public static boolean isWhitespace(String token) {
        return token == null || token.trim().isEmpty();
    }    public static boolean isAlaphabet(String token){return ALPHABET.contains(token);}
    public static boolean isNumber(String token){return NUMBER.contains(token);}
    public static boolean isKeyword(String token){
        for(int i=0;i<KEYWORD.length;i++){
            if (KEYWORD[i].equals(token))
                return true;
        }
        return false;
    }
    public static boolean isSeparator(String token) {
        return SEPARATOR.contains(token);
    }
    public static boolean isOperator(String token){
        return OPERATOR.contains(token);
    }
    public static String getOperatorName(String symbol){
        switch (symbol){
            case "=":return "Assignment OP";
            case "--":
            case "++":
            case"!": return "Unary OP";
            case"+":
            case "-":
            case"/":
            case "*":
            case "%": return "Arithmetic OP";
            case "<":
            case ">":
            case "==":
            case "<=":
            case ">=":
            case "!=": return "Relation OP";
            case "&&":
            case "||": return "Logical OP";
            default: return "???";
        }
    }
    public static enum Type {
        Identifier, Keyword, Operator, Separator, IntLiteral, RealLiteral, StrLiteral;
    }
}