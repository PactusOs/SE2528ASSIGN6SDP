import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TreeIntegrityTest {
    @Test
    void rejectsNegativeSizesAndInvalidNames() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("a", -1));
        assertThrows(IllegalArgumentException.class, () -> new FileNode("a/b", 4));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryNode("  "));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryNode(".."));
        assertThrows(IllegalArgumentException.class, () -> new FileNode(null, 2));
    }
    @Test
    void rejectsDuplicateNamesAmongSiblingTypes() {
        DirectoryNode root = new DirectoryNode("root");
        root.add(new FileNode("same", 1));
        assertThrows(IllegalArgumentException.class, () -> root.add(new DirectoryNode("same")));
        assertThrows(IllegalArgumentException.class, () -> root.add(new FileNode("same", 8)));
        assertEquals(1, root.getChildren().size());
    }
    @Test
    void rejectsTwoParentsForSameChild() {
        DirectoryNode a = new DirectoryNode("a");
        DirectoryNode b = new DirectoryNode("b");
        FileNode file = new FileNode("file", 1);
        a.add(file);
        assertThrows(IllegalArgumentException.class, () -> b.add(file));
        assertSame(a, file.getParent());
        assertTrue(b.getChildren().isEmpty());
    }
    @Test
    void rejectsSelfOwnershipAndAncestorCycles() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode child = new DirectoryNode("child");
        root.add(child);
        assertThrows(IllegalArgumentException.class, () -> root.add(root));
        assertThrows(IllegalArgumentException.class, () -> child.add(root));
        assertThrows(IllegalArgumentException.class, () -> child.add(child));
        assertEquals(List.of(child), root.getChildren());
    }
    @Test
    void doesNotExposeMutableChildren() {
        DirectoryNode root = new DirectoryNode("root");
        root.add(new FileNode("safe", 1));
        assertThrows(UnsupportedOperationException.class,
                () -> root.getChildren().add(new FileNode("unsafe", 2)));
        assertEquals(1, root.getChildren().size());
    }
    @Test
    void permitsValidDeepNestingAndNameReuseInDifferentDirectories() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode left = new DirectoryNode("left");
        DirectoryNode right = new DirectoryNode("right");
        root.add(left);
        root.add(right);
        left.add(new FileNode("shared.txt", 10));
        right.add(new FileNode("shared.txt", 20));
        TotalVisitor size = new TotalVisitor();
        new FileSystem().walk(root, size);
        assertEquals(30, size.getTotalSize());
    }
    @Test
    void validatesSearchThresholdAndVisitorInput() {
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor(name -> true, -1));
        assertThrows(NullPointerException.class, () -> new SearchVisitor(null, 0));
        assertThrows(NullPointerException.class,
                () -> new FileSystem().walk(null, new TotalVisitor()));
    }
}
