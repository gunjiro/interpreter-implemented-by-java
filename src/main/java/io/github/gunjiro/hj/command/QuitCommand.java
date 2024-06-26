package io.github.gunjiro.hj.command;

public class QuitCommand implements Command {
    @Override
    public <R> R accept(Command.Visitor<R> visitor) {
        return visitor.visit(this);
    }
}