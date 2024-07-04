package io.github.gunjiro.hj;

class EmptyRequest implements Request {
    @Override
    public <R> R accept(Request.Visitor<R> visitor) {
        return visitor.visit(this);
    }
}