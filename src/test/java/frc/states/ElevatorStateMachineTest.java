package frc.states;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ElevatorStateMachineTest {
    ElevatorStateMachine elevatorStateMachine;

    @Before
    public void tmp() {
        elevatorStateMachine = new ElevatorStateMachine();
    }

    @Test
    public void getFilteredEncoder() {
        Assert.assertEquals(0.0, elevatorStateMachine.getEncoderFiltered(), 1E-10);
    }
}
