package io.github.gunjiro.hj;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import io.github.gunjiro.hj.ResourceProvider.FailedException;
import io.github.gunjiro.hj.command.CommandAnalyzer;
import io.github.gunjiro.hj.command.executor.CommandExecutor;
import io.github.gunjiro.hj.processor.FileLoader;

public class AppRequestOperator {
    private final ResourceProvider provider;
    private final StringPrinter stringPrinter;
    private final MessagePrinter messagePrinter;

    private final Implementor implementor;
    private final List<Observer> observers = new LinkedList<>();

    public static interface Implementor {
        public void quit();
    }

    public static interface Observer {
        public void notifyQuit();
    }

    public AppRequestOperator(ResourceProvider provider, StringPrinter strinngPrinter, MessagePrinter messagePrinter, Implementor implementor) {
        this.provider = provider;
        this.stringPrinter = strinngPrinter;
        this.messagePrinter = messagePrinter;
        this.implementor = implementor;
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
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
                                    messagePrinter.printMessage(e.getMessage());
                                }
                            }

                            @Override
                            public void sendMessage(String message) {
                                messagePrinter.printMessage(message);
                            }
                            
                        }, new FileLoader.Factory() {

                            @Override
                            public Reader createReader(String filename) throws FileNotFoundException {
                                try {
                                    return provider.open(filename);
                                } catch (FailedException e) {
                                    throw new FileNotFoundException(e.getMessage());
                                }
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
                            messagePrinter.printMessage(String.format("unknown command '%s'", ((CommandExecutor.CommandIsUnknown)notification).getCommand()));
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
                                stringPrinter.print(output);
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
                        messagePrinter.printMessage(message);
                    }

                });
                action.take(environment, request);
                return null;
            }
        });
    }
}
