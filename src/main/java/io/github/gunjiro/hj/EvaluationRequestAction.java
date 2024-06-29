package io.github.gunjiro.hj;
import java.io.StringReader;

public class EvaluationRequestAction {
    private final Implementor implementor;

    public static interface Implementor {
        public void print(Value value);
        public void printMessage(String message);
    }

    public EvaluationRequestAction(Implementor implementor) {
        this.implementor = implementor;
    }

    public void take(Environment environment, EvaluationRequest request) {
        take(environment, request.getInput());
    }

    private void take(Environment environment, String code) {
        if (code.isEmpty()) {
            return;
        }

        try {
            implementor.print(createThunk(environment, code).eval());
            implementor.printMessage("");
        } catch (ApplicationException e) {
            implementor.printMessage("");
            implementor.printMessage(e.getMessage());
        } catch (EvaluationException e) {
            implementor.printMessage("");
            implementor.printMessage(e.getMessage());
        }
    }

    private Thunk createThunk(Environment environment, String code) throws ApplicationException {
        return environment.createThunk(new StringReader(code));
    }
}
