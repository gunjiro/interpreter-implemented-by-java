public class QuitCommandAction {
    public void take(QuitCommand command) throws ExitException {
        throw new ExitException();
    }
}
