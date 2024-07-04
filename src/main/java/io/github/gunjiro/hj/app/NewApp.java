package io.github.gunjiro.hj.app;

import io.github.gunjiro.hj.DefaultEnvironment;
import io.github.gunjiro.hj.REPL;
import io.github.gunjiro.hj.InputReceiver;
import io.github.gunjiro.hj.ui.OutputOperation;

public class NewApp {
    public static void run() {
        createREPL().run();
    }

    private static REPL createREPL() {
        return REPL.create(new DefaultEnvironment(), new OutputOperation(), new InputReceiver(), new AppInformation());
    }
}
