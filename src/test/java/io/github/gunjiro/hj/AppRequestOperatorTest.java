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

        final AppRequestOperator operator = new AppRequestOperator(null, null, null, new AppRequestOperator.Implementor() {

            @Override
            public void quit() {
                result.append(".....quit.....");
            }

        });
        operator.operate(new DefaultEnvironment(), request);

        assertThat(result.toString(), is(".....quit....."));
    }
    
}
