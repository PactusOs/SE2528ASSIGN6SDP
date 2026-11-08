import java.util.ArrayList;
import java.util.List;

public class DirectoryNode implements Element {
    private final String name;
    private final List<Element> children = new ArrayList<>();
    private DirectoryNode parent;

    public DirectoryNode(String name) {
        if (name == null || name.isBlank() || name.contains("/") || name.equals(".") || name.equals("..")) {
            throw new IllegalArgumentException("Invalid directory name");
        }
        this.name = name;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public DirectoryNode getParent() {
        return parent;
    }
    void setParent(DirectoryNode parent) {
        this.parent = parent;
    }
    @Override
    public List<Element> getChildren() {
        return List.copyOf(children);
    }
    public void add(FileNode file) {
        checkChild(file);
        children.add(file);
        file.setParent(this);
    }
    public void add(DirectoryNode directory) {
        checkChild(directory);
        children.add(directory);
        directory.setParent(this);
    }
    private void checkChild(Element child) {
        if (child == null) {
            throw new IllegalArgumentException("Child cannot be null");
        }
        if (child.getParent() != null) {
            throw new IllegalArgumentException("Child already belongs to a directory");
        }
        for (Element current : children) {
            if (current.getName().equals(child.getName())) {
                throw new IllegalArgumentException("Duplicate child name: " + child.getName());
            }
        }
        for (DirectoryNode current = this; current != null; current = current.getParent()) {
            if (current == child) {
                throw new IllegalArgumentException("Cannot create a cycle");
            }
        }
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
