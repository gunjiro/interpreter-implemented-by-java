package io.github.gunjiro.hj.command.action;

import io.github.gunjiro.hj.command.LoadCommand;

public class NewLoadCommandAction {
    public static interface Implementor {
        public void load(String name);
    }

    private final Implementor implementor;

    public NewLoadCommandAction(Implementor implementor) {
        this.implementor = implementor;
    }

    public void take(LoadCommand command) {
        for (String name : command.getResourceNames()) {
            take(name);
        }
    }

    private void take(String name) {
        implementor.load(name);
    }
}
