package io.github.gunjiro.hj;
public class IOLoop {
    private final RequestFactory factory;
    private final InputReceiver receiver;
    private final AppRequestOperator operator;
    private boolean isExited = false;

    private IOLoop(RequestFactory factory ,InputReceiver receiver, AppRequestOperator operator) {
        this.factory = factory;
        this.receiver = receiver;
        this.operator = operator;
    }

    public static IOLoop create(InputReceiver receiver, AppRequestOperator operator) {
        final IOLoop ioLoop = new IOLoop(new RequestFactory(), receiver, operator);
        operator.addObserver(new AppRequestOperator.Observer() {

            @Override
            public void notifyQuit() {
                ioLoop.isExited = true;
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

            if (isExited) {
                break;
            }
        }
    }
}
