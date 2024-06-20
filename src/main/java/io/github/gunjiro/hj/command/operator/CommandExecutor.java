package io.github.gunjiro.hj.command.operator;

import io.github.gunjiro.hj.ExitException;
import io.github.gunjiro.hj.command.Command;
import io.github.gunjiro.hj.command.EmptyCommand;
import io.github.gunjiro.hj.command.LoadCommand;
import io.github.gunjiro.hj.command.QuitCommand;
import io.github.gunjiro.hj.command.UnknownCommand;

public class CommandExecutor {
    private final Implementor implementor;

    public CommandExecutor(Implementor implementor) {
        this.implementor = implementor;
    }

    public static interface Implementor {
        public void execute(EmptyCommand command);
        public void execute(QuitCommand command);
        public void execute(LoadCommand command);
        public void execute(UnknownCommand command);
        public void exit();
    }

    public void execute(Command command) {
        try {
            command.accept(new Command.Visitor<Void>() {

                @Override
                public Void visit(EmptyCommand command) {
                    implementor.execute(command);
                    return null;
                }

                @Override
                public Void visit(QuitCommand command) throws ExitException {
                    implementor.execute(command);
                    return null;
                }

                @Override
                public Void visit(LoadCommand command) {
                    implementor.execute(command);
                    return null;
                }

                @Override
                public Void visit(UnknownCommand command) {
                    implementor.execute(command);
                    return null;
                }
            });
        } catch (ExitException e) {
            implementor.exit();
        }
    }
}
