package io.github.gunjiro.hj.app;

public class AppInformation {
    private State state = State.RUNNING;

    public static enum State {
        RUNNING,
        STOPPING;

        public boolean isRunning() {
            return this.equals(RUNNING);
        }
    }

    public void changeStopping() {
        state = State.STOPPING;
    }

    public State getState() {
        assert (state != null) : "..... state is null .....";
        return state;
    }

}
