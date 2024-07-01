package io.github.gunjiro.hj;

import io.github.gunjiro.hj.command.CommandAnalyzer;
import io.github.gunjiro.hj.command.executor.CommandExecutor;

public class AppRequestOperator {
    private final Implementor implementor;
    private final Environment environment;

    public static interface Implementor {
        public void load(String name);
        public void quit();
        public void print(String output);
        public void sendMessage(String message);
    }

    public AppRequestOperator(Implementor implementor, Environment environment) {
        this.implementor = implementor;
        this.environment = environment;
    }

    public void operate(Request request) {
        request.accept(new Request.Visitor<Void>() {

            @Override
            public Void visit(EmptyRequest request) {
                return null;
            }

            @Override
            public Void visit(CommandRequest request) {
                final CommandExecutor executor = new CommandExecutor(new CommandExecutor.Implementor() {

                    @Override
                    public void load(String name) {
                        implementor.load(name);
                    }

                    @Override
                    public void quit() {
                        implementor.quit();
                    }

                });

                executor.addObserver(new CommandExecutor.Observer() {

                    @Override
                    public void receive(CommandExecutor.Notification notification) {
                        if (notification instanceof CommandExecutor.CommandIsUnknown) {
                            implementor.sendMessage(String.format("unknown command '%s'", ((CommandExecutor.CommandIsUnknown)notification).getCommand()));
                        }
                    }

                });

                final CommandAnalyzer analyzer = new CommandAnalyzer();
                executor.execute(analyzer.analyze(request.getInput()));

                return null;
            }

            @Override
            public Void visit(EvaluationRequest request) {
                final EvaluationRequestAction action = new EvaluationRequestAction(new EvaluationRequestAction.Implementor() {

                    @Override
                    public void print(Value value) {
                        final ValuePrinter printer = new ValuePrinter(new ValuePrinter.Implementor() {

                            @Override
                            public void print(String output) {
                                implementor.print(output);
                            }

                        });

                        try {
                            printer.print(value);
                        } catch (ApplicationException e) {
                            printMessage("");
                            printMessage(e.getMessage());
                        }
                    }

                    @Override
                    public void printMessage(String message) {
                        implementor.sendMessage(message);
                    }

                });
                action.take(environment, request);
                return null;
            }
        });
    }
}
