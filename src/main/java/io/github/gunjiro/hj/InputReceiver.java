package io.github.gunjiro.hj;
import java.io.InputStreamReader;

import java.io.BufferedReader;
import java.io.IOException;

public class InputReceiver {

    public String receive() throws IOException {
        return createLineReader().read(createBufferedReader());
    }

    private static LineReader createLineReader() {
        return new LineReader();
    }

    private static BufferedReader createBufferedReader() {
        return new BufferedReader(new InputStreamReader(System.in));
    }

}