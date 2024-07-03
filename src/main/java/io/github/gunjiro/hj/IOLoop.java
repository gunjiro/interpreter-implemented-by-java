package io.github.gunjiro.hj;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringReader;

import io.github.gunjiro.hj.ResourceProvider.FailedException;
import io.github.gunjiro.hj.processor.FileLoader;

public class IOLoop {
    private final RequestFactory factory;
    private final InputReceiver receiver;
    private final AppRequestOperator operator;
    private final State state;

    private static class State {
        private boolean isExited = false;

        private void quit() {
            isExited = true;
        }

        private boolean isExited() {
            return isExited;
        }
    }

    private IOLoop(RequestFactory factory ,InputReceiver receiver, AppRequestOperator operator, State state) {
        this.factory = factory;
        this.receiver = receiver;
        this.operator = operator;
        this.state = state;
    }

    /**
     * @param receiver
     * @param provider
     * @param stringPrinter
     * @param messagePrinter
     * @return
     */
    public static IOLoop create(InputReceiver receiver, ResourceProvider provider, StringPrinter stringPrinter, MessagePrinter messagePrinter, Environment environment) {
        final State state = new State();
        final AppRequestOperator operator = new AppRequestOperator(new AppRequestOperator.Implementor() {

            @Override
            public void quit() {
                state.quit();
            }

            @Override
            public void sendText(String output) {
                stringPrinter.print(output);
            }

            @Override
            public void sendMessage(String message) {
                messagePrinter.printMessage(message);
            }

            @Override
            public void load(String name) {
                final FileLoader loader = new FileLoader(new FileLoader.Implementor() {

                    @Override
                    public void storeFunctions(Reader reader) {
                        try {
                            environment.addFunctions(reader);
                        } catch (ApplicationException e) {
                            sendMessage(e.getMessage());
                        }
                    }

                    @Override
                    public Reader open(String filename) throws FileNotFoundException {
                        try {
                            return provider.open(filename);
                        } catch (FailedException e) {
                            throw new FileNotFoundException(e.getMessage());
                        }
                    }

                });
                loader.addObserver(new FileLoader.Observer() {

                    @Override
                    public void receiveMessage(String message) {
                        sendMessage(message);
                    }
                    
                });
                loader.load(name);
            }

            @Override
            public void sendBreak() {
                messagePrinter.printMessage("");
            }
            
        }, new AppRequestOperator.Factory() {

            @Override
            public Thunk createThunk(String code) throws ApplicationException {
                return environment.createThunk(new StringReader(code));
            }
            
        });
        return new IOLoop(new RequestFactory(), receiver, operator, state);
    }

    public void loop() {
        while (true) {
            String input = receiver.receive();
            Request request = factory.createRequest(input);
            operator.operate(request);

            if (state.isExited()) {
                break;
            }
        }
    }
}
