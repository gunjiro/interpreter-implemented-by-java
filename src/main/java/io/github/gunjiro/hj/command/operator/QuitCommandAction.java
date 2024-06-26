package io.github.gunjiro.hj.command.operator;

import java.util.LinkedList;
import java.util.List;

import io.github.gunjiro.hj.command.QuitCommand;

class QuitCommandAction {
    private final List<Observer> observers;

    public static interface Observer {
        public void notifyQuit();
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

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void take(QuitCommand command) {
        notifyQuitAll();
    }

    private void notifyQuitAll() {
        for (Observer observer : observers) {
            observer.notifyQuit();
        }
    }
}
