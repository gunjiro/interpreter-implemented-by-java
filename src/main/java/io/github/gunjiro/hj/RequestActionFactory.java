package io.github.gunjiro.hj;

public class RequestActionFactory {
    public EvaluationRequestAction createEvaluationRequestAction(StringPrinter stringPrinter, MessagePrinter messagePrinter) {
        return new EvaluationRequestAction(new ValuePrinter(new ValuePrinter.Implementor() {

            @Override
            public void print(String output) {
                stringPrinter.print(output);
            }
            
        }), messagePrinter);
    }
}
