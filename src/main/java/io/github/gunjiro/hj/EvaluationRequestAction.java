package io.github.gunjiro.hj;
import java.io.StringReader;

public class EvaluationRequestAction {
    private final Implementor implementor;

    public static interface Implementor {
        public void print(Value value);
        public void printMessage(String message);
    }

    public EvaluationRequestAction(ValuePrinter valuePrinter, MessagePrinter messagePrinter) {
        this.implementor = new Implementor() {

            @Override
            public void print(Value value) {
                try {
                    valuePrinter.print(value);
                } catch (ApplicationException e) {
                    assert false;
                }
            }

            @Override
            public void printMessage(String message) {
                messagePrinter.printMessage(message);
            }
            
        };
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
