public class LexicalScanner {
    public static final String[] KEYWORD = {"boolean ", "break", "continue", "else", "for", "float", "if", "int", "return", "void", "while"};
    public static final String SYMBOL="& | + - * / % = < > ! [ ] { } ( ) . , ; : ";
    public static final String ALPHABET="a b c d e f g h i j k l m n o p q r s t u v w x y z _ " +
            "Q W E R T Y U I O P A S D F G H J K L Z X C V B N M";
    public static final String NUMBER=" 0 1 2 3 4 5 6 7 8 9";
    public static boolean isAlaphabet(String token){return ALPHABET.contains(token);}
    public static boolean isNumber(String token){return NUMBER.contains(token);}
    public static boolean isKeyword(String token){
        for(int i=0;i<KEYWORD.length;i++){
            if (KEYWORD[i].equals(token))
                return true;
        }
        return false;
    }
    public static boolean isSymbol(String token){
        return SYMBOL.contains(token);
    }
    public static String getSymbolName(String symbol){
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
            case "(": return "Left Bracket";
            case ")": return "Right Bracket";
            case "[": return "Left SqBracket";
            case "]": return "Right SqBracket";
            case "{": return "Left CurBracket";
            case "}": return "Right CurBracket";
            case ";": return "Semicolon";
            case ",": return "Comma";
            default: return "???";
        }
    }
    public static enum Type {
        Identifier, Keyword, Operator, Seperator, Literal;
    }
}