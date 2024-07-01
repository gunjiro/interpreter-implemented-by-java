package io.github.gunjiro.hj;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.FileNotFoundException;
import java.io.Reader;

import org.junit.Test;

public class AppRequestOperatorTest {
    @Test
    public void operateShouldExitByQuitCommand() {
        // :qで終了
        final Request request = new CommandRequest(":q");
        final StringBuilder result = new StringBuilder();

        final AppRequestOperator operator = new AppRequestOperator(new AppRequestOperator.Implementor() {

            @Override
            public void quit() {
                result.append(".....quit.....");
            }

            @Override
            public void print(String output) {
                throw new UnsupportedOperationException("Unimplemented method 'print'");
            }

            @Override
            public void sendMessage(String message) {
                throw new UnsupportedOperationException("Unimplemented method 'sendMessage'");
            }

        }, new AppRequestOperator.Factory() {

            @Override
            public Reader createReader(String filename) throws FileNotFoundException {
                throw new UnsupportedOperationException("Unimplemented method 'createReader'");
            }

        });
        operator.operate(new DefaultEnvironment(), request);

        assertThat(result.toString(), is(".....quit....."));
    }
    
}
