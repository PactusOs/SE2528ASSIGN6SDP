import java.util.Objects;

public class FileSystem {
    public void walk(Element root, Visitor visitor) {
        Objects.requireNonNull(root);
        Objects.requireNonNull(visitor);
        visitor.begin(root);
        traverse(root, visitor);
    }
    private void traverse(Element element, Visitor visitor) {
        element.accept(visitor);
        for (Element child : element.getChildren()) {
            traverse(child, visitor);
        }
    }
}
