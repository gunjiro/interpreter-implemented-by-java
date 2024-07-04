package io.github.gunjiro.hj;

import java.io.Reader;
import java.io.StringReader;

import io.github.gunjiro.hj.app.AppInformation;
import io.github.gunjiro.hj.processor.FileLoader;
import io.github.gunjiro.hj.ui.OutputOperation;

public class REPL {
    public static interface Implementor {
        public String waitForInput();
        public void execute(String input);
        public void showQuitMessage();
        public boolean isRunning();
    }

    private final Implementor implementor;

    public REPL(Implementor implementor) {
        this.implementor = implementor;
    }

    public static REPL create(Environment environment, OutputOperation outOperation, InputReceiver receiver, AppInformation information) {
        return new REPL(new Implementor() {

            @Override
            public String waitForInput() {
                return receiver.receive();
            }

            @Override
            public void showQuitMessage() {
                outOperation.printMessage("Bye.");
            }

            @Override
            public void execute(String input) {
                operate(input);
            }

            private void operate(String input) {
                final Request request = createRequest(input);
                createOperator().operate(request);
            }

            private Request createRequest(String input) {
                final RequestFactory factory = new RequestFactory();
                return factory.createRequest(input);
            }

            private AppRequestOperator createOperator() {
                return new AppRequestOperator(new AppRequestOperator.Implementor() {

                    @Override
                    public void quit() {
                        information.changeStopping();
                    }

                    @Override
                    public void sendText(String text) {
                        outOperation.printText(text);
                    }

                    @Override
                    public void sendMessage(String message) {
                        outOperation.printMessage(message);
                    }

                    @Override
                    public void load(String name) {
                        final FileLoader loader = new FileLoader(new FileLoader.DefaultImplementor() {

                            @Override
                            public void storeFunctions(Reader reader) {
                                try {
                                    environment.addFunctions(reader);
                                } catch (ApplicationException e) {
                                    outOperation.printMessage(e.getMessage());
                                }
                            }

                        });
                        loader.addObserver(new FileLoader.Observer() {

                            @Override
                            public void receiveMessage(String message) {
                                outOperation.printMessage(message);
                            }

                        });
                        loader.load(name);
                    }

                    @Override
                    public void sendBreak() {
                        outOperation.startANewLine();
                    }

                }, new AppRequestOperator.Factory() {

                    @Override
                    public Thunk createThunk(String code) throws ApplicationException {
                        return environment.createThunk(new StringReader(code));
                    }

                });
            }

            @Override
            public boolean isRunning() {
                return information.isStateRunning();
            }
        });
    }

    public void run() {
        do {
            final String input = implementor.waitForInput();
            implementor.execute(input);
        } while (implementor.isRunning());

        implementor.showQuitMessage();
    }

}
