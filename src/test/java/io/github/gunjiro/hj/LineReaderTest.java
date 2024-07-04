package io.github.gunjiro.hj;
import static org.junit.Assert.assertThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LineReaderTest {

    @Test
    public void throwsIOExceptionIfReadLineReturnsNull() throws IOException {
        // ストリームの終わりに達している場合IOExceptionを投げる

        final LineReader reader = new LineReader(new LineReader.Factory() {

            @Override
            public BufferedReader createBufferedReader() {
                return new BufferedReader(new StringReader(""));
            }
            
        });

        IOException thrown = assertThrows(IOException.class, () -> reader.read());
        assertThat(thrown.getMessage(), is("the end of the stream has been reached"));
    }

}
