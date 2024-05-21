import java.util.ArrayList;
import java.util.List;

public class ASTNode {
    String value;
    List<ASTNode> children;

    ASTNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    void addChild(ASTNode child) {
        children.add(child);
    }

    @Override
    public String toString() {
        if (children.isEmpty()) {
            return value;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("(").append(value);
            for (ASTNode child : children) {
                sb.append(" ").append(child.toString());
            }
            sb.append(")");
            return sb.toString();
        }
    }
}
