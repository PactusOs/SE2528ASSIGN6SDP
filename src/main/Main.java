public class Main {
    public static void main(String[] args) {
        DirectoryNode root = new DirectoryNode("workspace");
        DirectoryNode src = new DirectoryNode("src");
        DirectoryNode backup = new DirectoryNode("backup");
        DirectoryNode assets = new DirectoryNode("assets");

        root.add(new FileNode("README.md", 300));
        root.add(src);
        root.add(assets);
        root.add(new DirectoryNode("empty"));
        src.add(new FileNode("App.java", 150));
        src.add(new FileNode("Utils.java", 90));
        src.add(backup);
        backup.add(new FileNode("OldApp.java", 50));
        assets.add(new FileNode("logo.png", 700));

        FileSystem walker = new FileSystem();
        TotalVisitor size = new TotalVisitor();
        SearchVisitor search = new SearchVisitor(name -> name.endsWith(".java"), 100);
        StatisticsVisitor stats = new StatisticsVisitor();
        walker.walk(root, size);
        walker.walk(root, search);
        walker.walk(root, stats);

        System.out.println("Total size: " + size.getTotalSize() + " bytes");
        System.out.println("Java files (min 100 bytes): " + search.getMatches());
        System.out.println("Files: " + stats.getFileCount());
        System.out.println("Directories: " + stats.getDirectoryCount());
        System.out.println("Max depth: " + stats.getMaxDepth());
    }
}
