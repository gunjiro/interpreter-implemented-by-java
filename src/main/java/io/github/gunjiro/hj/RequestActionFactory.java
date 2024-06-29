package io.github.gunjiro.hj;

public class RequestActionFactory {
    public EvaluationRequestAction createEvaluationRequestAction(StringPrinter stringPrinter, MessagePrinter messagePrinter) {
        return new EvaluationRequestAction(new EvaluationRequestAction.Implementor() {

            @Override
            public void print(Value value) {
                final ValuePrinter printer = new ValuePrinter(new ValuePrinter.Implementor() {

                    @Override
                    public void print(String output) {
                        stringPrinter.print(output);
                    }
                    
                });

                try {
                    printer.print(value);
                } catch (ApplicationException e) {
                    assert false;
                }
            }

            @Override
            public void printMessage(String message) {
                messagePrinter.printMessage(message);
            }
            
        });
    }
}
