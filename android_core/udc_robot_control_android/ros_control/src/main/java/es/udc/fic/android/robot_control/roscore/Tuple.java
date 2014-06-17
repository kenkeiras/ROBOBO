package es.udc.fic.android.robot_control.roscore;

public class Tuple<T, M> {
    public final T x;
    public final M y;
    public Tuple(T x, M y) {
        this.x = x;
        this.y = y;
    }
}
