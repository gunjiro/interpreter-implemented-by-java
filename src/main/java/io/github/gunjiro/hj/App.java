package io.github.gunjiro.hj;

public class App {
    private final IOLoop ioLoop;
    private final MessagePrinter printer;

    App(IOLoop ioLoop, MessagePrinter printer) {
        this.ioLoop = ioLoop;
        this.printer = printer;
    }

    public void run() {
        ioLoop.loop();
        printer.printMessage("Bye.");
    }
}
