package io.github.gunjiro.hj.app;

import java.io.Reader;
import java.io.StringReader;

import io.github.gunjiro.hj.AppRequestOperator;
import io.github.gunjiro.hj.ApplicationException;
import io.github.gunjiro.hj.DefaultEnvironment;
import io.github.gunjiro.hj.Environment;
import io.github.gunjiro.hj.InputReceiver;
import io.github.gunjiro.hj.REPL;
import io.github.gunjiro.hj.Request;
import io.github.gunjiro.hj.RequestFactory;
import io.github.gunjiro.hj.SystemInInputReceiver;
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
    public void showQuitMessage() {
        printMessage("Bye.");
    }

    @Override
    public REPL.Result execute(String input) {
        operate(input);
        return isExited ? REPL.Result.Quit : REPL.Result.Continue;
    }

    private void printMessage(String message) {
        System.out.println(message);
    }

    private void printText(String text) {
        System.out.print(text);
    }

    private void startANewLine() {
        System.out.println();
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
        return new AppRequestOperator(new AppRequestOperator.Implementor() {

            @Override
            public void quit() {
                isExited = true;
            }

            @Override
            public void sendText(String text) {
                printText(text);
            }
           
            @Override
            public void sendMessage(String message) {
                printMessage(message);
            }

            @Override
            public void load(String name) {
                final FileLoader loader = FileLoader.create(new FileLoader.Implementor() {

                    @Override
                    public void storeFunctions(Reader reader) {
                        try {
                            environment.addFunctions(reader);
                        } catch (ApplicationException e) {
                            printMessage(e.getMessage());
                        }
                    }
                    
                });
                loader.addObserver(new FileLoader.Observer() {

                    @Override
                    public void receiveMessage(String message) {
                        printMessage(message);
                    }

                });
                loader.load(name);
            }

            @Override
            public void sendBreak() {
                startANewLine();
            }

        }, new AppRequestOperator.Factory() {

            @Override
            public Thunk createThunk(String code) throws ApplicationException {
                return environment.createThunk(new StringReader(code));
            }
            
        });
    }

}