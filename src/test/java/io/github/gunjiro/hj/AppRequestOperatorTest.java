package io.github.gunjiro.hj;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

            @Override
            public void load(String name) {
                throw new UnsupportedOperationException("Unimplemented method 'load'");
            }

        }, new DefaultEnvironment());
        operator.operate(request);

        assertThat(result.toString(), is(".....quit....."));
    }
    
}
