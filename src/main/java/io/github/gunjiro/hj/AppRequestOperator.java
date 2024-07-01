package io.github.gunjiro.hj;

import java.io.FileNotFoundException;
import java.io.Reader;
import io.github.gunjiro.hj.command.CommandAnalyzer;
import io.github.gunjiro.hj.command.executor.CommandExecutor;
import io.github.gunjiro.hj.processor.FileLoader;

public class AppRequestOperator {
    private final Implementor implementor;
    private final Factory factory;

    public static interface Implementor {
        public void quit();
        public void print(String output);
        public void sendMessage(String message);
    }

    public static interface Factory {
        public Reader createReader(String filename) throws FileNotFoundException;
    }

    public AppRequestOperator(Implementor implementor, Factory factory) {
        this.implementor = implementor;
        this.factory = factory;
    }

    public void operate(Environment environment, Request request) {
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
                        final FileLoader loader = new FileLoader(new FileLoader.Implementor() {

                            @Override
                            public void storeFunctions(Reader reader) {
                                try {
                                    environment.addFunctions(reader);
                                } catch (ApplicationException e) {
                                    implementor.sendMessage(e.getMessage());
                                }
                            }

                            @Override
                            public void sendMessage(String message) {
                                implementor.sendMessage(message);
                            }
                            
                        }, new FileLoader.Factory() {

                            @Override
                            public Reader createReader(String filename) throws FileNotFoundException {
                                return factory.createReader(filename);
                            }
                            
                        });
                        loader.load(name);
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
