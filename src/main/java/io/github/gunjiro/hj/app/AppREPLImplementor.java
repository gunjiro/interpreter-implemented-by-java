package io.github.gunjiro.hj.app;

import java.io.FileNotFoundException;
import java.io.Reader;

import io.github.gunjiro.hj.AppRequestOperator;
import io.github.gunjiro.hj.DefaultEnvironment;
import io.github.gunjiro.hj.Environment;
import io.github.gunjiro.hj.FileResourceProvider;
import io.github.gunjiro.hj.InputReceiver;
import io.github.gunjiro.hj.MessagePrinter;
import io.github.gunjiro.hj.REPL;
import io.github.gunjiro.hj.Request;
import io.github.gunjiro.hj.RequestFactory;
import io.github.gunjiro.hj.ResourceProvider;
import io.github.gunjiro.hj.ResourceProvider.FailedException;
import io.github.gunjiro.hj.StringPrinter;
import io.github.gunjiro.hj.SystemInInputReceiver;
import io.github.gunjiro.hj.SystemOutMessagePrinter;
import io.github.gunjiro.hj.SystemOutStringPrinter;

class AppREPLImplementor implements REPL.Implementor {
    private final Environment environment;
    private boolean isExited = false;

    private AppREPLImplementor(Environment environment) {
        this.environment = environment;
    }

    static AppREPLImplementor create() {
        return new AppREPLImplementor(new DefaultEnvironment());
    }

    @Override
    public String waitForInput() {
        final InputReceiver receiver = SystemInInputReceiver.create();
        return receiver.receive();
    }

    @Override
    public REPL.Result execute(String input) {
        operate(input);
        return isExited ? REPL.Result.Quit : REPL.Result.Continue;
    }

    private void operate(String input) {
        final Request request = createRequest(input);
        createOperator().operate(environment, request);
    }

    private static Request createRequest(String input) {
        final RequestFactory factory = new RequestFactory();
        return factory.createRequest(input);
    }

    private AppRequestOperator createOperator() {
        final StringPrinter stringPrinter = new SystemOutStringPrinter();
        final MessagePrinter messagePrinter = new SystemOutMessagePrinter();
        final ResourceProvider provider = new FileResourceProvider();
        return new AppRequestOperator(new AppRequestOperator.Implementor() {

            @Override
            public void quit() {
                isExited = true;
            }

            @Override
            public void print(String output) {
                stringPrinter.print(output);
            }
           
            @Override
            public void sendMessage(String message) {
                messagePrinter.printMessage(message);
            }

        }, new AppRequestOperator.Factory() {

            @Override
            public Reader createReader(String filename) throws FileNotFoundException {
                try {
                    return provider.open(filename);
                } catch (FailedException e) {
                    throw new FileNotFoundException(e.getMessage());
                }
            }
            
        });
    }

    @Override
    public void showQuitMessage() {
        System.out.println("Bye.");
    }

}