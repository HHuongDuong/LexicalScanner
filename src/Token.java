public class Token {
    LexicalScanner.Type type;
    String value;

    public Token(LexicalScanner.Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public LexicalScanner.Type getType() {
        return type;
    }

    public void setType(LexicalScanner.Type type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
