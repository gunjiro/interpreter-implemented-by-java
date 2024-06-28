package io.github.gunjiro.hj;
public class ValuePrinterFactory {
    public ValuePrinter create() {
        return new ValuePrinter(new ValuePrinter.Implementor() {

            @Override
            public void print(String output) {
                System.out.print(output);
            }
            
        });
    }
}
