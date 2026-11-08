# Visitor Pattern-Advanced File System



## Run

From the project root:

```bash
mvn test
mvn compile exec:java
```

The application entry point is `Main`. The Java files are directly inside `src/main` and `src/test`, with those source directories configured in `pom.xml`.

## How it works

- `Element`: common contract for files and directories.
- `FileNode`, `DirectoryNode`: tree nodes, with one parent per child.
- `Visitor`: overloaded methods for each concrete node type.
- `FileSystem`: the single traversal owner (preorder DFS).
- `TotalVisitor`: adds file sizes.
- `SearchVisitor`: accepts `Predicate<String>` for filenames and an inclusive minimum size; sorts relative paths.
- `StatisticsVisitor`: counts files, directories and levels, with root depth 0.

`walker.walk(root, visitor)` calls `begin(root)` to reset state, then calls `accept(visitor)` once on every reachable node. This allows reuse of a visitor on the same or a different root without stale results. To search only for files ending in `.java`, use `new SearchVisitor(name -> name.endsWith(".java"), 100)`.

Tree integrity is enforced when calling `DirectoryNode.add(...)`. Null children, duplicate sibling names, self/ancestor cycles and multiple parent ownership are rejected. Directory children are returned as a non-writable snapshot. File size and name are immutable. `Main` uses fixed sample data, with no input required.


## Tests

`VisitorPatternTest` and `TreeIntegrityTest` cover the eight required checkpoints and additional cases. Run `mvn test` after Maven dependencies are available.


## Repository


