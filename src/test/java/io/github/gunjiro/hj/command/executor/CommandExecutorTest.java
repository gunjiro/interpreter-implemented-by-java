package io.github.gunjiro.hj.command.executor;

import org.junit.Test;

import io.github.gunjiro.hj.UnknownCommandAction;
import io.github.gunjiro.hj.command.LoadCommand;
import io.github.gunjiro.hj.command.QuitCommand;
import io.github.gunjiro.hj.command.UnknownCommand;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.LinkedList;
import java.util.List;

public class CommandExecutorTest {
    @Test
    public void outputsMessageWhenInputIsUnknownCommand() {
        // 入力が不明なコマンドの場合、メッセージを出力する。
        // このテストでは「コマンド処理」の結果が「不明なコマンドを処理するアクション」の結果と同等になることを確認する。
        final UnknownCommand input = new UnknownCommand("☆☆☆☆☆");
        final StringBuilder outputByOperator = new StringBuilder();
        final StringBuilder outputByAction = new StringBuilder();

        final CommandExecutor executor = new CommandExecutor(new CommandExecutor.Implementor() {

            @Override
            public void showMessage(String message) {
                outputByOperator.append(message);
            }

            @Override
            public void load(String name) {
                throw new UnsupportedOperationException("Unimplemented method 'load'");
            }
            
        });

        final UnknownCommandAction action = new UnknownCommandAction(new UnknownCommandAction.Implementor() {

            @Override
            public void showMessage(String message) {
                outputByAction.append(message);
            }
            
        });

        executor.execute(input);
        action.take(input);

        assertThat(outputByOperator.toString(), is(outputByAction.toString()));
    }

    @Test
    public void outputsMessagesAfterOperatingLoadCommand() {
        // 入力が読み込みコマンドの場合、リソースから関数定義等を読み込む。
        // このテストでは「コマンド処理」の結果が「読み込みコマンドを処理するアクション」の結果と同等になることを確認する。
        final LoadCommand input = new LoadCommand(List.of("resource1", "resource2"));
        final LinkedList<String> outputsByOperator = new LinkedList<String>();
        final LinkedList<String> outputsByAction = new LinkedList<String>();
        final CommandExecutor executor = new CommandExecutor(new CommandExecutor.Implementor() {

            @Override
            public void showMessage(String message) {
                throw new UnsupportedOperationException("Unimplemented method 'showMessage'");
            }

            @Override
            public void load(String name) {
                outputsByOperator.add("loaded: " + name);
            }

        });
            
        final LoadCommandAction action = new LoadCommandAction(new LoadCommandAction.Implementor() {

            @Override
            public void load(String name) {
                outputsByAction.add("loaded: " + name);
            }
        });

        executor.execute(input);
        action.take(input);

        assertThat(outputsByOperator, is(outputsByAction));
    }

    @Test
    public void receivesQuitEventWhenExecutesQuitCommand() {
        // 終了コマンドを実行すると通知を受け取る
        final StringBuilder message = new StringBuilder();

        final CommandExecutor executor = new CommandExecutor(new CommandExecutor.Implementor() {

            @Override
            public void showMessage(String message) {
                throw new UnsupportedOperationException("Unimplemented method 'showMessage'");
            }

            @Override
            public void load(String name) {
                throw new UnsupportedOperationException("Unimplemented method 'load'");
            }
            
        });
        executor.addObserver(new CommandExecutor.Observer() {

            @Override
            public void receiveQuitEvent() {
                message.append(".....quit.....");
            }
            
        });
        executor.execute(new QuitCommand());

        assertThat(message.toString(), is(".....quit....."));
    }
}
