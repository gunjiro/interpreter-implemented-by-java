package io.github.gunjiro.hj;
import java.io.InputStreamReader;

import java.io.BufferedReader;

public class SystemInInputReceiver implements InputReceiver {

    @Override
    public String receive() {
        final LineReader reader = new LineReader();
        return reader.read(createBufferedReader());
    }

    private BufferedReader createBufferedReader() {
        return new BufferedReader(new InputStreamReader(System.in));
    }

}