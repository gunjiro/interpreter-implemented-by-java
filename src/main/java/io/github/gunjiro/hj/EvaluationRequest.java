package io.github.gunjiro.hj;

class EvaluationRequest implements Request {
    private final String input;

    EvaluationRequest(String in) {
        input = in;
    }

    public String getInput() {
        return input;
    }

    @Override
    public <R> R accept(Request.Visitor<R> visitor) {
        return visitor.visit(this);
    }
}