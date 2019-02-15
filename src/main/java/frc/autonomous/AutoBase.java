package frc.autonomous;

public abstract class AutoBase {
    public abstract void init();

    public abstract void periodic(double timestamp);

    public abstract boolean isDone();
}
