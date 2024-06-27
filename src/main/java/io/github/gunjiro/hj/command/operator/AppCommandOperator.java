package io.github.gunjiro.hj.command.operator;

import java.util.LinkedList;
import java.util.List;

import io.github.gunjiro.hj.UnknownCommandAction;
import io.github.gunjiro.hj.command.Command;
import io.github.gunjiro.hj.command.EmptyCommand;
import io.github.gunjiro.hj.command.LoadCommand;
import io.github.gunjiro.hj.command.QuitCommand;
import io.github.gunjiro.hj.command.UnknownCommand;

public class AppCommandOperator implements CommandOperator {
    private final Implementor implementor;
    private final List<Observer> observers = new LinkedList<>();

    public static interface Implementor {
        public void showMessage(String message);
        public void load(String name);
    }

    public static interface Observer {
        public void notifyQuit();
    }

    public AppCommandOperator(Implementor implementor) {
        this.implementor = implementor;
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void operate(Command command) {
        final CommandExecutor executor = new CommandExecutor(new CommandExecutor.Implementor() {

            @Override
            public void execute(EmptyCommand command) {
            }

            @Override
            public void execute(QuitCommand command) {
                operate(command);
            }

            @Override
            public void execute(LoadCommand command) {
                operate(command);
            }

            @Override
            public void execute(UnknownCommand command) {
                operate(command);
            }
            
        });

        executor.execute(command);
    }

    private void operate(QuitCommand command) {
        createQuitCommandAction().take(command);
    }

    private void operate(LoadCommand command) {
        createLoadCommandAction().take(command);
    }

    private void operate(UnknownCommand command) {
        createUnknownCommandAction().take(command);
    }

    private QuitCommandAction createQuitCommandAction() {
        final QuitCommandAction action = QuitCommandAction.create();
        for (Observer observer : observers) {
            action.addObserver(new QuitCommandAction.Observer() {

                @Override
                public void notifyQuit() {
                    observer.notifyQuit();
                }

            });
        }
        return action;
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
