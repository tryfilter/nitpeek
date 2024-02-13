package nitpeek.io.testutil;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtil {
    private FileUtil() {}

    public static Path testFile(FileSystem fileSystem, String dirName, String fileName) throws IOException {
        var testDir = testDir(fileSystem, dirName);
        Files.createDirectory(testDir);
        return testDir.resolve(fileName);
    }

    public static Path testDir(FileSystem fileSystem, String dirName) {
        return fileSystem.getPath(dirName);
    }

}
