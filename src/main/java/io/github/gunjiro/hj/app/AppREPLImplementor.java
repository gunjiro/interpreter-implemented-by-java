package io.github.gunjiro.hj.app;

import java.io.Reader;
import java.io.StringReader;

import io.github.gunjiro.hj.AppRequestOperator;
import io.github.gunjiro.hj.ApplicationException;
import io.github.gunjiro.hj.DefaultEnvironment;
import io.github.gunjiro.hj.Environment;
import io.github.gunjiro.hj.InputReceiver;
import io.github.gunjiro.hj.MessagePrinter;
import io.github.gunjiro.hj.REPL;
import io.github.gunjiro.hj.Request;
import io.github.gunjiro.hj.RequestFactory;
import io.github.gunjiro.hj.StringPrinter;
import io.github.gunjiro.hj.SystemInInputReceiver;
import io.github.gunjiro.hj.SystemOutMessagePrinter;
import io.github.gunjiro.hj.SystemOutStringPrinter;
import io.github.gunjiro.hj.Thunk;
import io.github.gunjiro.hj.processor.FileLoader;

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
        createOperator().operate(request);
    }

    private static Request createRequest(String input) {
        final RequestFactory factory = new RequestFactory();
        return factory.createRequest(input);
    }

    private AppRequestOperator createOperator() {
        final StringPrinter stringPrinter = new SystemOutStringPrinter();
        final MessagePrinter messagePrinter = new SystemOutMessagePrinter();
        return new AppRequestOperator(new AppRequestOperator.Implementor() {

            @Override
            public void quit() {
                isExited = true;
            }

            @Override
            public void sendText(String text) {
                stringPrinter.print(text);
            }
           
            @Override
            public void sendMessage(String message) {
                messagePrinter.printMessage(message);
            }

            @Override
            public void load(String name) {
                FileLoader.create(new FileLoader.Implementor() {

                    @Override
                    public void storeFunctions(Reader reader) {
                        try {
                            environment.addFunctions(reader);
                        } catch (ApplicationException e) {
                            sendMessage(e.getMessage());
                        }
                    }

                    @Override
                    public void sendMessage(String message) {
                        messagePrinter.printMessage(message);
                    }
                    
                }).load(name);
            }

        }, new AppRequestOperator.Factory() {

            @Override
            public Thunk createThunk(String code) throws ApplicationException {
                return environment.createThunk(new StringReader(code));
            }
            
        });
    }

    @Override
    public void showQuitMessage() {
        System.out.println("Bye.");
    }

}