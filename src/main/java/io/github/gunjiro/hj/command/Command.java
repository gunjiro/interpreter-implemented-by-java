package io.github.gunjiro.hj.command;

public interface Command {
    public <R> R accept(Visitor<R> visitor);

    public static interface Visitor<R> {
        public R visit(EmptyCommand command);
        public R visit(QuitCommand command);
        public R visit(LoadCommand command);
        public R visit(UnknownCommand command);
    }
}
