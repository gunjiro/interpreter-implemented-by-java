package io.github.gunjiro.hj;

import org.junit.Test;

import io.github.gunjiro.hj.app.AppInformation;
import io.github.gunjiro.hj.ui.OutputOperation;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class REPLTest {

    @Test
    public void verifyLoop() {
        // REPLは入力と実行の繰り返しを制御する。
        // このテストでは４回、空の入力をしたあと、５回目の終了コマンドで終了することを検証する。
        final Deque<String> messages = new LinkedList<>();

        final Deque<String> inputs = new LinkedList<>(List.of("", "", "", "", ":q"));
        final REPL repl = REPL.create(new DefaultEnvironment(), new OutputOperation(), new InputReceiver() {

            @Override
            public String receive() {
                assert !inputs.isEmpty() : "..... already received all inputs .....";
                messages.add("..... received .....");
                return inputs.pop();
            }
            
        }, new AppInformation());

        repl.run();

        assertThat(messages, hasSize(5));
    }

}
