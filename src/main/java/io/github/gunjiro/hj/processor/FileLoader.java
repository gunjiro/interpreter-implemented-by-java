package io.github.gunjiro.hj.processor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

public class FileLoader {
    private final Implementor implementor;
    private final Factory factory;
    private final List<Observer> observers = new LinkedList<>();

    public static interface Implementor {
        public void storeFunctions(Reader reader);
    }

    public static interface Factory {
        public Reader createReader(String filename) throws FileNotFoundException;
    }

    public static interface Observer {
        public void receiveMessage(String message);
    }

    public FileLoader(Implementor implementor, Factory factory) {
        this.implementor = implementor;
        this.factory = factory;
    }

    public static FileLoader create(Implementor implementor) {
        return new FileLoader(implementor, new Factory() {

            @Override
            public Reader createReader(String filename) throws FileNotFoundException {
                return new FileReader(filename);
            }
            
        });
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void load(String filename) {
        try (Reader reader = factory.createReader(filename)) {
            implementor.storeFunctions(reader);
            notifyObserversOfMessage("loaded: " + filename);
        } catch (FileNotFoundException e) {
            notifyObserversOfMessage(e.getMessage());
        } catch (IOException e) {
            notifyObserversOfMessage(e.getMessage());
        }
    }

    private void notifyObserversOfMessage(String message) {
        for (Observer observer : observers) {
            observer.receiveMessage(message);
        }
    }
}
