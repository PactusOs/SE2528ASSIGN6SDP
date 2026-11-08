import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class VisitorPatternTest {
    private final FileSystem walker = new FileSystem();
    @Test
    void dispatchesToCorrectOverloads() {
        DirectoryNode root = new DirectoryNode("root");
        root.add(new FileNode("a.txt", 10));
        List<String> calls = new ArrayList<>();
        Visitor visitor = new Visitor() {
            public void begin(Element root) {
                calls.clear();
            }
            public void visit(FileNode file) {
                calls.add("file:" + file.getName());
            }
            public void visit(DirectoryNode directory) {
                calls.add("directory:" + directory.getName());
            }
        };
        walker.walk(root, visitor);
        assertEquals(List.of("directory:root", "file:a.txt"), calls);
    }
    @Test
    void addsFileSizesAcrossMultipleLevels() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode a = new DirectoryNode("a");
        DirectoryNode b = new DirectoryNode("b");
        root.add(new FileNode("first", 12));
        root.add(a);
        a.add(new FileNode("second", 34));
        a.add(b);
        b.add(new FileNode("third", 56));
        TotalVisitor visitor = new TotalVisitor();
        walker.walk(root, visitor);
        assertEquals(102, visitor.getTotalSize());
    }

    @Test
    void combinesNameCriterionAndInclusiveSize() {
        DirectoryNode root = new DirectoryNode("root");
        root.add(new FileNode("notes.txt", 80));
        root.add(new FileNode("large.txt", 100));
        root.add(new FileNode("large.jpg", 180));
        root.add(new FileNode("small.txt", 99));
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".txt"), 100);
        walker.walk(root, visitor);
        assertEquals(List.of("large.txt"), visitor.getMatches());
    }
    @Test
    void sortsSearchPathsAndUsesChosenRoot() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode z = new DirectoryNode("z");
        DirectoryNode a = new DirectoryNode("a");
        root.add(z);
        root.add(a);
        z.add(new FileNode("second.md", 4));
        a.add(new FileNode("first.md", 3));
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".md"), 0);
        walker.walk(root, visitor);
        assertEquals(List.of("a/first.md", "z/second.md"), visitor.getMatches());
        walker.walk(z, visitor);
        assertEquals(List.of("second.md"), visitor.getMatches());
    }
    @Test
    void countsRootDirectoriesAndMaximumDepth() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode sub = new DirectoryNode("sub");
        DirectoryNode deep = new DirectoryNode("deep");
        root.add(sub);
        sub.add(deep);
        deep.add(new FileNode("end", 1));
        root.add(new DirectoryNode("empty"));
        StatisticsVisitor visitor = new StatisticsVisitor();
        walker.walk(root, visitor);
        assertEquals(1, visitor.getFileCount());
        assertEquals(4, visitor.getDirectoryCount());
        assertEquals(3, visitor.getMaxDepth());
    }
    @Test
    void handlesEmptyDirectoryAndNoSearchMatches() {
        DirectoryNode root = new DirectoryNode("root");
        TotalVisitor size = new TotalVisitor();
        SearchVisitor search = new SearchVisitor(name -> name.endsWith(".json"), 200);
        StatisticsVisitor stats = new StatisticsVisitor();
        walker.walk(root, size);
        walker.walk(root, search);
        walker.walk(root, stats);
        assertEquals(0, size.getTotalSize());
        assertEquals(List.of(), search.getMatches());
        assertEquals(0, stats.getFileCount());
        assertEquals(1, stats.getDirectoryCount());
        assertEquals(0, stats.getMaxDepth());
    }
    @Test
    void resetsAllVisitorsBeforeEveryExecution() {
        DirectoryNode first = new DirectoryNode("first");
        first.add(new FileNode("first.txt", 5));
        DirectoryNode second = new DirectoryNode("second");
        second.add(new FileNode("second.txt", 7));
        TotalVisitor size = new TotalVisitor();
        SearchVisitor search = new SearchVisitor(name -> name.endsWith(".txt"), 0);
        StatisticsVisitor stats = new StatisticsVisitor();
        walker.walk(first, size);
        walker.walk(first, search);
        walker.walk(first, stats);
        walker.walk(second, size);
        walker.walk(second, search);
        walker.walk(second, stats);
        assertEquals(7, size.getTotalSize());
        assertEquals(List.of("second.txt"), search.getMatches());
        assertEquals(1, stats.getFileCount());
        assertEquals(1, stats.getDirectoryCount());
        walker.walk(second, size);
        walker.walk(second, search);
        walker.walk(second, stats);
        assertEquals(7, size.getTotalSize());
        assertEquals(List.of("second.txt"), search.getMatches());
        assertEquals(1, stats.getFileCount());
    }

    @Test
    void visitsStandaloneFileAtDepthZero() {
        FileNode root = new FileNode("alone.txt", 8);
        SearchVisitor search = new SearchVisitor(name -> name.equals("alone.txt"), 8);
        StatisticsVisitor stats = new StatisticsVisitor();
        walker.walk(root, search);
        walker.walk(root, stats);
        assertEquals(List.of("alone.txt"), search.getMatches());
        assertEquals(1, stats.getFileCount());
        assertEquals(0, stats.getDirectoryCount());
        assertEquals(0, stats.getMaxDepth());
    }

    @Test
    void allowsZeroSizeFileAndChecksMinimumSize() {
        DirectoryNode root = new DirectoryNode("root");
        root.add(new FileNode("zero.dat", 0));
        SearchVisitor search = new SearchVisitor(name -> true, 0);
        walker.walk(root, search);
        assertEquals(List.of("zero.dat"), search.getMatches());
    }
}
