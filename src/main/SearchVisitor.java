import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class SearchVisitor implements Visitor {
    private final Predicate<String> nameCriterion;
    private final long minimumSize;
    private final List<String> matches = new ArrayList<>();
    private Element root;
    public SearchVisitor(Predicate<String> nameCriterion, long minimumSize) {
        this.nameCriterion = Objects.requireNonNull(nameCriterion);
        if (minimumSize < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }
        this.minimumSize = minimumSize;
    }
    @Override
    public void begin(Element root) {
        this.root = root;
        matches.clear();
    }
    @Override
    public void visit(FileNode file) {
        if (nameCriterion.test(file.getName()) && file.getSize() >= minimumSize) {
            matches.add(relativePath(file));
        }
    }
    @Override
    public void visit(DirectoryNode directory) {
    }
    private String relativePath(FileNode file) {
        List<String> parts = new ArrayList<>();
        for (Element current = file; current != root; current = current.getParent()) {
            parts.add(current.getName());
        }
        Collections.reverse(parts);
        return parts.isEmpty() ? file.getName() : String.join("/", parts);
    }
    public List<String> getMatches() {
        List<String> sorted = new ArrayList<>(matches);
        Collections.sort(sorted);
        return List.copyOf(sorted);
    }
}
