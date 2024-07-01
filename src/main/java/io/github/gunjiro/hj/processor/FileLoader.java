package io.github.gunjiro.hj.processor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class FileLoader {
    private final Implementor implementor;
    private final Factory factory;

    public static interface Implementor {
        public void storeFunctions(Reader reader);
        public void sendMessage(String message);
    }

    public static interface Factory {
        public Reader createReader(String filename) throws FileNotFoundException;
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

    public void load(String filename) {
        try (Reader reader = factory.createReader(filename)) {
            implementor.storeFunctions(reader);
            implementor.sendMessage("loaded: " + filename);
        } catch (FileNotFoundException e) {
            implementor.sendMessage(e.getMessage());
        } catch (IOException e) {
            implementor.sendMessage(e.getMessage());
        }
    }

}
