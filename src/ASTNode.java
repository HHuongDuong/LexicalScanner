import java.util.ArrayList;
import java.util.List;

class ASTNode {
    private String type;
    private String value;
    private List<ASTNode> children;

    public ASTNode(String type, String value) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(ASTNode child) {
        children.add(child);
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public List<ASTNode> getChildren() {
        return children;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(type);
        if (value != null) {
            sb.append(" ").append(value);
        }
        for (ASTNode child : children) {
            sb.append(" ").append(child.toString());
        }
        sb.append(")");
        return sb.toString();
    }
}