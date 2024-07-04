package io.github.gunjiro.hj;
import java.io.InputStreamReader;

import java.io.BufferedReader;

public class SystemInInputReceiver implements InputReceiver {

    @Override
    public String receive() {
        return createLineReader().read(createBufferedReader());
    }

    private static LineReader createLineReader() {
        return new LineReader();
    }

    private static BufferedReader createBufferedReader() {
        return new BufferedReader(new InputStreamReader(System.in));
    }

}