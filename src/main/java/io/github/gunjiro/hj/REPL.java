package io.github.gunjiro.hj;

import java.io.Reader;
import java.io.StringReader;

import io.github.gunjiro.hj.processor.FileLoader;
import io.github.gunjiro.hj.ui.OutputOperation;

public class REPL {
    public static interface Implementor {
        /**
         * 入力待ちをする。
         * @return 入力された文字列
         */
        public String waitForInput();

        /**
         * 入力された文字列を解析して実行する。
         * @param input 入力された文字列
         * @return 入力待ちを繰り返すか終了するか
         */
        public REPL.Result execute(String input);

        /**
         * 終了時のメッセージを表示する。
         */
        public void showQuitMessage();
    }
    
    public static enum Result {
        Continue,
        Quit,
    }

    private final Implementor implementor;

    /**
     * @param implementor 外部で定義する実装
     */
    public REPL(Implementor implementor) {
        this.implementor = implementor;
    }

    public static REPL create(Environment environment, OutputOperation outOperation) {
        return new REPL(new Implementor() {
            private boolean isExited = false;

            @Override
            public String waitForInput() {
                final InputReceiver receiver = SystemInInputReceiver.create();
                return receiver.receive();
            }

            @Override
            public void showQuitMessage() {
                outOperation.printMessage("Bye.");
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

            private Request createRequest(String input) {
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
                        outOperation.printText(text);
                    }

                    @Override
                    public void sendMessage(String message) {
                        outOperation.printMessage(message);
                    }

                    @Override
                    public void load(String name) {
                        final FileLoader loader = FileLoader.create(new FileLoader.Implementor() {

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
        });
    }

    /**
     * REPLを実行する。
     */
    public void run() {
        REPL.Result result = REPL.Result.Quit;

        do {
            final String input = implementor.waitForInput();
            result = implementor.execute(input);
        } while (REPL.Result.Continue.equals(result));

        implementor.showQuitMessage();
    }

}
