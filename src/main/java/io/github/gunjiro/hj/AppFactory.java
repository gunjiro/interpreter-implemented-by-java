package io.github.gunjiro.hj;
public class AppFactory {
    public App create(InputReceiver receiver, ResourceProvider provider, StringPrinter stringPrinter, MessagePrinter messagePrinter, Environment environment) {
        return new App(IOLoop.create(receiver, provider, stringPrinter, messagePrinter, environment), messagePrinter);
    }
}
