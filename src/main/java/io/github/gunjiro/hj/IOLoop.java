package io.github.gunjiro.hj;
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
    public static IOLoop create(InputReceiver receiver, ResourceProvider provider, StringPrinter stringPrinter, MessagePrinter messagePrinter) {
        final State state = new State();
        final AppRequestOperator operator = new AppRequestOperator(provider, stringPrinter, messagePrinter, new AppRequestOperator.Implementor() {

            @Override
            public void quit() {
                state.quit();
            }
            
        });
        final IOLoop ioLoop = new IOLoop(new RequestFactory(), receiver, operator, state);
        operator.addObserver(new AppRequestOperator.Observer() {

            @Override
            public void notifyQuit() {
                state.quit();
            }
            
        });
        return ioLoop;
    }

    public void loop() {
        final Environment environment = new DefaultEnvironment();
        while (true) {
            String input = receiver.receive();
            Request request = factory.createRequest(input);
            operator.operate(environment, request);

            if (state.isExited()) {
                break;
            }
        }
    }
}
