package io.github.gunjiro.hj;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class LineReaderTest {
    @Test(expected = IOException.class)
    public void throwsIOExceptionIfReadLineReturnsNull() throws IOException {
        // readLineでnullが返されたら、IOExceptionを投げる
        final BufferedReader br = new BufferedReader(new StringReader("")) {
            @Override
            public String readLine() throws IOException {
                return null;
            }
        };
        final LineReader reader = new LineReader();

        reader.read(br);
    }
}
