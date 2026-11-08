public class TotalVisitor implements Visitor {
    private long totalSize;
    @Override
    public void begin(Element root) {
        totalSize = 0;
    }
    @Override
    public void visit(FileNode file) {
        totalSize = Math.addExact(totalSize, file.getSize());
    }
    @Override
    public void visit(DirectoryNode directory) {
    }
    public long getTotalSize() {
        return totalSize;
    }
}
