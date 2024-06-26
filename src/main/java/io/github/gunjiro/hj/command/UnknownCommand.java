package io.github.gunjiro.hj.command;

public class UnknownCommand implements Command {
    private final String commandName;

    public UnknownCommand(String name) {
        commandName = name;
    }

    public String getCommandName() {
        return commandName;
    }

    @Override
    public <R> R accept(Command.Visitor<R> visitor) {
        return visitor.visit(this);
    }
}