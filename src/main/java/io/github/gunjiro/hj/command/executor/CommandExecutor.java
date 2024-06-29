package io.github.gunjiro.hj.command.executor;

import java.util.LinkedList;
import java.util.List;

import io.github.gunjiro.hj.UnknownCommandAction;
import io.github.gunjiro.hj.command.Command;
import io.github.gunjiro.hj.command.EmptyCommand;
import io.github.gunjiro.hj.command.LoadCommand;
import io.github.gunjiro.hj.command.QuitCommand;
import io.github.gunjiro.hj.command.UnknownCommand;

public class CommandExecutor {
    private final Implementor implementor;
    private final List<Observer> observers = new LinkedList<>();

    public static interface Implementor {
        public void showMessage(String message);
        public void load(String name);
    }

    public static interface Observer {
        public void receive(Notification notification);
    }

    public static interface Notification { }
    public static class CommandIsEmpty implements Notification { }
    public static class CommandIsUnknown implements Notification { }
    public static class Quit implements Notification { }

    public CommandExecutor(Implementor implementor) {
        this.implementor = implementor;
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void execute(Command command) {
        createCommandOperator().operate(command);
    }

    private CommandOperator createCommandOperator() {
        return new CommandOperator(new CommandOperator.Implementor() {

            @Override
            public void execute(EmptyCommand command) {
            }

            @Override
            public void execute(QuitCommand command) {
                notifyObservers(new Quit());
            }

            @Override
            public void execute(LoadCommand command) {
                createLoadCommandAction().take(command);
            }

            @Override
            public void execute(UnknownCommand command) {
                createUnknownCommandAction().take(command);
            }
            
        });
    }

    private void notifyObservers(Notification notification) {
        for (Observer observer : observers) {
            observer.receive(notification);
        }
    }

    private LoadCommandAction createLoadCommandAction() {
        return new LoadCommandAction(new LoadCommandAction.Implementor() {
            @Override
            public void load(String name) {
                implementor.load(name);
            }
        });
    }

    private UnknownCommandAction createUnknownCommandAction() {
        return new UnknownCommandAction(new UnknownCommandAction.Implementor() {

            @Override
            public void showMessage(String message) {
                implementor.showMessage(message);
            }

        });
    }
}
