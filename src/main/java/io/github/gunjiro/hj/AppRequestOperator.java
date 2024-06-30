package io.github.gunjiro.hj;

import java.io.IOError;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import io.github.gunjiro.hj.command.CommandAnalyzer;
import io.github.gunjiro.hj.command.executor.CommandExecutor;

public class AppRequestOperator {
    private final ResourceProvider provider;
    private final StringPrinter stringPrinter;
    private final MessagePrinter messagePrinter;
    private final List<Observer> observers = new LinkedList<>();
    public static interface Observer {
        public void notifyQuit();
    }

    private AppRequestOperator(ResourceProvider provider, StringPrinter strinngPrinter, MessagePrinter messagePrinter) {
        this.provider = provider;
        this.stringPrinter = strinngPrinter;
        this.messagePrinter = messagePrinter;
    }

    public static AppRequestOperator create(ResourceProvider provider, StringPrinter strinngPrinter, MessagePrinter messagePrinter) {
        final AppRequestOperator operator = new AppRequestOperator(provider, strinngPrinter, messagePrinter);
        operator.addObserver(new Observer() {

            @Override
            public void notifyQuit() {
            }
            
        });
        return operator;
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
                        try (Reader reader = provider.open(name)) {
                            environment.addFunctions(reader);
                            messagePrinter.printMessage("loaded: " + name);
                        } catch (ResourceProvider.FailedException e) {
                            messagePrinter.printMessage(e.getMessage());
                        } catch (ApplicationException e) {
                            messagePrinter.printMessage(e.getMessage());
                        } catch (IOException e) {
                            throw new IOError(e);
                        }
                    }

                    @Override
                    public void quit() {
                        for (Observer observer : observers) {
                            observer.notifyQuit();
                        }
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
