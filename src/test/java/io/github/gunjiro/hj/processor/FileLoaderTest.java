package io.github.gunjiro.hj.processor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class FileLoaderTest {
    @Test
    public void sendsMessageIfFileNotFound() {
        // ファイルが見つからない場合、メッセージを送る。
        final StringBuilder output = new StringBuilder();

        final FileLoader loader = new FileLoader(new FileLoader.Implementor() {

            @Override
            public void storeFunctions(Reader reader) {
                throw new UnsupportedOperationException("Unimplemented method 'storeFunctions'");
            }

            @Override
            public Reader open(String filename) throws FileNotFoundException {
                throw new FileNotFoundException("..... file not found .....");
            }

        });
        loader.addObserver(new FileLoader.Observer() {

            @Override
            public void receiveMessage(String message) {
                output.append(message);
            }
            
        });
        loader.load(".....filename.....");

        assertThat(output, hasToString("..... file not found ....."));
    }

    @Test
    public void storeFunctionsFromReader() {
        // Readerから関数を取り込む。
        final List<String> messages = new LinkedList<>();
        final FileLoader loader = new FileLoader(new FileLoader.Implementor() {

            @Override
            public void storeFunctions(Reader reader) {
                messages.add("..... stored .....");
            }

            @Override
            public Reader open(String filename) throws FileNotFoundException {
                return new StringReader(".....code.....");
            }

        });
        loader.addObserver(new FileLoader.Observer() {

            @Override
            public void receiveMessage(String message) {
                messages.add(message);
            }

        });
        loader.load("..... filename .....");

        assertThat(messages, contains("..... stored .....", "loaded: ..... filename ....."));
    }

    @Test
    public void sendMessageIfThrowsIOExceptionByClose() {
        // Readerを閉じたときにIOExceptionが発生したら、メッセージを送る。
        final List<String> messages = new LinkedList<>();

        final FileLoader loader = new FileLoader(new FileLoader.Implementor() {

            @Override
            public void storeFunctions(Reader reader) {
            }

            @Override
            public Reader open(String filename) throws FileNotFoundException {
                return new Reader() {

                    @Override
                    public int read(char[] cbuf, int off, int len) throws IOException {
                        throw new UnsupportedOperationException("Unimplemented method 'read'");
                    }

                    @Override
                    public void close() throws IOException {
                        throw new IOException("..... throws io exception by close .....");
                    }
                };
            }

        });
        loader.addObserver(new FileLoader.Observer() {

            @Override
            public void receiveMessage(String message) {
                messages.add(message);
            }
            
        });
        loader.load("..... filename .....");

        assertThat(messages, hasItem("..... throws io exception by close ....."));
    }
}
