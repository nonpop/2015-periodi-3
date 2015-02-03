package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * A little wrapper around <code>FileInputStream</code> to allow resetting.
 * Marking is not supported; reset() always goes back to the beginning of the file.
 */
public class ResettableFileInputStream extends InputStream {
    private final String path;
    private FileInputStream inf;

    public ResettableFileInputStream(String path) throws FileNotFoundException {
        this.path = path;
        inf = new FileInputStream(path);
    }

    @Override
    public int read() throws IOException {
        return inf.read();
    }

    @Override
    public void close() throws IOException {
        inf.close();
    }

    /**
     * Re-opens the file.
     * @throws FileNotFoundException 
     */
    @Override
    public void reset() throws FileNotFoundException {
        inf = new FileInputStream(path);
    }
}
