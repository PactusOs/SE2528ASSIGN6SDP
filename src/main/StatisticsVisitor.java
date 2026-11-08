public class StatisticsVisitor implements Visitor {
    private Element root;
    private int fileCount;
    private int directoryCount;
    private int maxDepth;

    @Override
    public void begin(Element root) {
        this.root = root;
        fileCount = 0;
        directoryCount = 0;
        maxDepth = 0;
    }
    @Override
    public void visit(FileNode file) {
        fileCount++;
        updateDepth(file);
    }
    @Override
    public void visit(DirectoryNode directory) {
        directoryCount++;
        updateDepth(directory);
    }
    private void updateDepth(Element element) {
        int depth = 0;
        for (Element current = element; current != root; current = current.getParent()) {
            depth++;
        }
        maxDepth = Math.max(maxDepth, depth);
    }
    public int getFileCount() {
        return fileCount;
    }
    public int getDirectoryCount() {
        return directoryCount;
    }
    public int getMaxDepth() {
        return maxDepth;
    }
}
