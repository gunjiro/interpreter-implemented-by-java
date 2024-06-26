package io.github.gunjiro.hj.command.operator;

import java.util.LinkedList;
import java.util.List;

import io.github.gunjiro.hj.ExitException;
import io.github.gunjiro.hj.command.QuitCommand;

class QuitCommandAction {
    private final List<Observer> observers;

    public QuitCommandAction() {
        this(newObservers());
    }

    private QuitCommandAction(List<Observer> observers) {
        this.observers = observers;
    }

    public static QuitCommandAction create() {
        return new QuitCommandAction(newObservers());
    }

    private static List<Observer> newObservers() {
        return new LinkedList<Observer>();
    }

    public static interface Observer {
        public void notifyQuit();
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void take(QuitCommand command) throws ExitException {
        notifyQuitAll();
        throw new ExitException();
    }

    private void notifyQuitAll() {
        for (Observer observer : observers) {
            observer.notifyQuit();
        }
    }
}
