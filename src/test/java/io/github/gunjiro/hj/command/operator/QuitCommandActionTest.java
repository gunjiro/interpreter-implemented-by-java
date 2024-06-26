package io.github.gunjiro.hj.command.operator;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import io.github.gunjiro.hj.ExitException;
import io.github.gunjiro.hj.command.QuitCommand;

public class QuitCommandActionTest {
    @Test
    public void notifiesObservers() {
        // Observerに終了を通知する
        final StringBuilder output = new StringBuilder();

        final QuitCommandAction action = QuitCommandAction.create();
        action.addObserver(new QuitCommandAction.Observer() {

            @Override
            public void notifyQuit() {
                output.append("☆☆☆☆☆ quit ☆☆☆☆☆");
            }
            
        });
        try {
            action.take(new QuitCommand());
        } catch (ExitException e) {
        }

        assertThat(output.toString(), is("☆☆☆☆☆ quit ☆☆☆☆☆"));
    }
}
