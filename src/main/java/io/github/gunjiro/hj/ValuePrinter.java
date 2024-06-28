package io.github.gunjiro.hj;
public class ValuePrinter {
    private final Implementor implementor;

    public static interface Implementor {
        public void print(String output);
    }

    public ValuePrinter(StringPrinter printer) {
        this.implementor = new Implementor() {

            @Override
            public void print(String output) {
                printer.print(output);
            }
            
        };
    }

    public void print(Value value) throws ApplicationException {
        if (value instanceof IntValue) {
            printInt((IntValue)value);
        }
        else if (value instanceof ListValue) {
            printList((ListValue)value);
        }
        else {
            throw new ApplicationException("Unsupported Type For Printing");
        }
    }

    private void printInt(IntValue value) {
        implementor.print(String.valueOf(value.getValue()));
    }

    private void printList(ListValue list) throws ApplicationException {
        try {
            implementor.print("[");
            if (!list.isEmpty()) {
                print(list.getHead());
                for (list = list.getTail(); !list.isEmpty(); list = list.getTail()) {
                    implementor.print(",");
                    print(list.getHead());
                }
            }
            implementor.print("]");
        }
        catch (EvaluationException e) {
            throw new ApplicationException(e);
        }
    }
}
