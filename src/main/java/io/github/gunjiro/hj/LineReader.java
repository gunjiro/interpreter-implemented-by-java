package io.github.gunjiro.hj;
import java.io.BufferedReader;
import java.io.IOException;

public class LineReader {
    public String read(BufferedReader reader) throws IOException {
        final String line = reader.readLine();
        if (line == null) {
            throw new IOException("the end of the stream has been reached");
        }
        return line;
    }
}
