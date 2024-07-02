package io.github.gunjiro.hj;

public class EvaluationRequestAction {
    private final Implementor implementor;
    private final Factory factory;

    public static interface Implementor {
        public void print(Value value);
        public void printMessage(String message);
    }

    public static interface Factory {
        public Thunk createThunk(Environment environment, String code) throws ApplicationException;
    }

    public EvaluationRequestAction(Implementor implementor, Factory factory) {
        this.implementor = implementor;
        this.factory = factory;
    }

    public void take(Environment environment, EvaluationRequest request) {
        take(environment, request.getInput());
    }

    private void take(Environment environment, String code) {
        if (code.isEmpty()) {
            return;
        }

        try {
            implementor.print(factory.createThunk(environment, code).eval());
            implementor.printMessage("");
        } catch (ApplicationException e) {
            implementor.printMessage("");
            implementor.printMessage(e.getMessage());
        } catch (EvaluationException e) {
            implementor.printMessage("");
            implementor.printMessage(e.getMessage());
        }
    }
}
