package io.github.gunjiro.hj;
import java.io.BufferedReader;
import java.io.IOException;

public class LineReader {
    private final Factory factory;

    public static interface Factory {
        public BufferedReader createBufferedReader();
    }

    public LineReader(Factory factory) {
        this.factory = factory;
    }

    public String read() throws IOException {
        final String line = factory.createBufferedReader().readLine();
        if (line == null) {
            throw new IOException("the end of the stream has been reached");
        }
        return line;
    }
}
