package io.github.gunjiro.hj;

public class EvaluationRequestAction {
    private final Implementor implementor;
    private final Factory factory;

    public static interface Implementor {
        public void sendValue(Value value);
        public void sendBreak();
        public void sendMessage(String message);
    }

    public static interface Factory {
        public Thunk createThunk(String code) throws ApplicationException;
    }

    public EvaluationRequestAction(Implementor implementor, Factory factory) {
        this.implementor = implementor;
        this.factory = factory;
    }

    public void take(EvaluationRequest request) {
        take(request.getInput());
    }

    private void take(String code) {
        if (code.isEmpty()) {
            return;
        }

        try {
            implementor.sendValue(factory.createThunk(code).eval());
            implementor.sendBreak();
        } catch (ApplicationException e) {
            implementor.sendBreak();
            implementor.sendMessage(e.getMessage());
        } catch (EvaluationException e) {
            implementor.sendBreak();
            implementor.sendMessage(e.getMessage());
        }
    }
}
