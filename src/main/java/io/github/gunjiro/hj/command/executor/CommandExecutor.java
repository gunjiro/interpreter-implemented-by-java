package io.github.gunjiro.hj.command.executor;

import java.util.LinkedList;
import java.util.List;

import io.github.gunjiro.hj.command.Command;
import io.github.gunjiro.hj.command.EmptyCommand;
import io.github.gunjiro.hj.command.LoadCommand;
import io.github.gunjiro.hj.command.QuitCommand;
import io.github.gunjiro.hj.command.UnknownCommand;

public class CommandExecutor {
    private final Implementor implementor;
    private final List<Observer> observers = new LinkedList<>();

    public static interface Implementor {
        public void load(String name);
        public void quit();
    }

    public static interface Observer {
        public void receive(Notification notification);
    }

    public static interface Notification { }
    public static class CommandIsEmpty implements Notification { }
    public static class CommandIsUnknown implements Notification {
        private final String command;

        public CommandIsUnknown(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((command == null) ? 0 : command.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CommandIsUnknown other = (CommandIsUnknown) obj;
            if (command == null) {
                if (other.command != null)
                    return false;
            } else if (!command.equals(other.command))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "CommandIsUnknown [command=" + command + "]";
        }
    }

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
                notifyObservers(new CommandIsEmpty());
            }

            @Override
            public void execute(QuitCommand command) {
                assert true;
                implementor.quit();
            }

            @Override
            public void execute(LoadCommand command) {
                createLoadCommandAction().take(command);
            }

            @Override
            public void execute(UnknownCommand command) {
                notifyObservers(new CommandIsUnknown(command.getCommandName()));
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
}
