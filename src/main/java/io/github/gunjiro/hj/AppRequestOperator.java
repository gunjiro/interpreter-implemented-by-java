package io.github.gunjiro.hj;

import java.io.IOError;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import io.github.gunjiro.hj.command.CommandAnalyzer;
import io.github.gunjiro.hj.command.operator.AppCommandOperator;

public class AppRequestOperator implements RequestOperator {
    private final RequestActionFactory factory;
    private final ResourceProvider provider;
    private final StringPrinter strinngPrinter;
    private final MessagePrinter messagePrinter;
    private final List<Observer> observers = new LinkedList<>();
    public static interface Observer {
        public void notifyQuit();
    }

    private AppRequestOperator(RequestActionFactory factory, ResourceProvider provider, StringPrinter strinngPrinter, MessagePrinter messagePrinter) {
        this.factory = factory;
        this.provider = provider;
        this.strinngPrinter = strinngPrinter;
        this.messagePrinter = messagePrinter;
    }

    public static AppRequestOperator create(ResourceProvider provider, StringPrinter strinngPrinter, MessagePrinter messagePrinter) {
        final AppRequestOperator operator = new AppRequestOperator(new RequestActionFactory(), provider, strinngPrinter, messagePrinter);
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

    @Override
    public void operate(Environment environment, Request request) {
        request.accept(new Request.Visitor<Void>() {

            @Override
            public Void visit(EmptyRequest request) {
                return null;
            }

            @Override
            public Void visit(CommandRequest request) {
                final AppCommandOperator operator = new AppCommandOperator(new AppCommandOperator.Implementor() {
                    @Override
                    public void showMessage(String message) {
                        messagePrinter.printMessage(message);
                    }

                    @Override
                    public void load(String name) {
                        try (Reader reader = provider.open(name)) {
                            environment.addFunctions(reader);
                            showMessage("loaded: " + name);
                        } catch (ResourceProvider.FailedException e) {
                            showMessage(e.getMessage());
                        } catch (ApplicationException e) {
                            showMessage(e.getMessage());
                        } catch (IOException e) {
                            throw new IOError(e);
                        }
                    }

                });

                for (Observer observer : observers) {
                    operator.addObserver(new AppCommandOperator.Observer() {

                        @Override
                        public void notifyQuit() {
                            observer.notifyQuit();
                        }

                    });
                }

                final CommandAnalyzer analyzer = new CommandAnalyzer();
                operator.operate(analyzer.analyze(request.getInput()));

                return null;
            }

            @Override
            public Void visit(EvaluationRequest request) {
                factory.createEvaluationRequestAction(strinngPrinter, messagePrinter).take(environment, request);
                return null;
            }
        });
    }
}
