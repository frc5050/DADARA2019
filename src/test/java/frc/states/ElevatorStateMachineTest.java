package frc.states;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static frc.loops.Looper.PERIOD;
import static frc.states.ElevatorStateMachine.*;

public class ElevatorStateMachineTest {
    /*

    Tests:
    Zeroes on bottom limit triggered

     */

  public static final double EPSILON = 1E-4;
  private ElevatorStateMachine elevatorStateMachine;
  private ElevatorStateMachine.ElevatorState elevatorState = new ElevatorStateMachine.ElevatorState();

  @Before
  public void setup() {
    elevatorStateMachine = new ElevatorStateMachine();
    elevatorState = new ElevatorStateMachine.ElevatorState();
  }
//

  /**
   * Zeroes the encoders no matter what position the height started at
   */
  @Test
  public void zeroes() {

    final double startHeightFake = 100;
    final double startHeightFakeEncoder = elevatorStateMachine.heightFromGroundToEncoder(100);
    double position = 0.4;
    final double deltaHeight = position - startHeightFake;
    elevatorStateMachine.setZeroing();
    elevatorState.encoder = startHeightFakeEncoder;
    double lastPosition = position;
    elevatorState = elevatorStateMachine.update(elevatorState);
    elevatorState.encoder = elevatorState.demand;
    double timestamp = 0.0;
    while (timestamp < 100.0 && elevatorState.state == ElevatorControlMode.ZEROING) {
      elevatorState = elevatorStateMachine.update(elevatorState);
      lastPosition = position;
      position = elevatorState.heightFromGround + deltaHeight;
      elevatorState.encoder = elevatorState.demand;
      elevatorState.bottomLimitTouched = position <= BOTTOM_DIST_FROM_GROUND;
      if (elevatorState.state == ElevatorControlMode.ZEROING) {
          Assert.assertEquals(ZEROING_VELOCITY * PERIOD, lastPosition - position, EPSILON);
      }
      timestamp += PERIOD;
    }
    Assert.assertEquals(elevatorState.heightFromGround, BOTTOM_DIST_FROM_GROUND, EPSILON);
    Assert.assertEquals(elevatorState.demand, elevatorStateMachine.heightFromGroundToEncoder(BOTTOM_DIST_FROM_GROUND), EPSILON);
  }

  /**
   * Ensures that the encoder zeroes properly.
   */
  @Test
  public void limitZeroing() {
    // TODO add in negative and zero offsets and a negative delta
    final double offsetValue = 100;
    final double deltaFromOffset = 20;

    Assert.assertEquals(0.0, elevatorState.encoderFiltered, EPSILON);
    elevatorState.bottomLimitTouched = true;

    elevatorState.encoder = offsetValue;
    elevatorState = elevatorStateMachine.update(elevatorState);
    Assert.assertEquals(0, elevatorState.encoderFiltered, EPSILON);

    elevatorState.bottomLimitTouched = false;
    elevatorState.encoder = offsetValue + deltaFromOffset;
    elevatorState = elevatorStateMachine.update(elevatorState);
    Assert.assertEquals(deltaFromOffset, elevatorState.encoderFiltered, EPSILON);
  }

  /**
   * Whatever you put in, you will get out.
   */
  @Test
  public void setOpenLoop() {
    final double percentage = 0.7;
    final double percentageNegative = -0.7;
    final double percentageZero = 0.0;
    final double insaneFeedForward = 100000.0;
    ElevatorStateMachine.ElevatorState elevatorState = new ElevatorStateMachine.ElevatorState();

    elevatorStateMachine.setOpenLoop(percentage);
    elevatorState.feedforward = insaneFeedForward;
    elevatorState = elevatorStateMachine.update(elevatorState);
    Assert.assertEquals(percentage, elevatorState.demand, EPSILON);
    Assert.assertEquals(0.0, elevatorState.feedforward, EPSILON);

    elevatorStateMachine.setOpenLoop(percentageNegative);
    elevatorState.feedforward = insaneFeedForward;
    elevatorState = elevatorStateMachine.update(elevatorState);
    Assert.assertEquals(percentageNegative, elevatorState.demand, EPSILON);
    Assert.assertEquals(0.0, elevatorState.feedforward, EPSILON);

    elevatorStateMachine.setOpenLoop(percentageZero);
    elevatorState.feedforward = insaneFeedForward;
    elevatorState = elevatorStateMachine.update(elevatorState);
    Assert.assertEquals(percentageZero, elevatorState.demand, EPSILON);
    Assert.assertEquals(0, elevatorState.feedforward, EPSILON);
  }
}
