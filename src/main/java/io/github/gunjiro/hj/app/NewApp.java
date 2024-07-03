package io.github.gunjiro.hj.app;

import io.github.gunjiro.hj.DefaultEnvironment;
import io.github.gunjiro.hj.REPL;

public class NewApp {
    public static void run() {
        createREPL().run();
    }

    private static REPL createREPL() {
        return REPL.create(new DefaultEnvironment());
    }
}
