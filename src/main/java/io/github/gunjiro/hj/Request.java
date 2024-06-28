package io.github.gunjiro.hj;

public interface Request {
    public <R> R accept(Visitor<R> visitor);

    public static interface Visitor<R> {
        public R visit(EmptyRequest request);
        public R visit(CommandRequest request);
        public R visit(EvaluationRequest request);
    }
}