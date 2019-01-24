package frc.subsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.utils.Constants;

import static frc.utils.Constants.HATCH_SHUFFLEBOARD;


// TODO pretty much this whole thing
//  invert whatever needs to be inverted
//  setup sensors &|| closed loop control for this
public class HatchMechanism extends Subsystem {
    private static HatchMechanism instance;
    private final WPI_TalonSRX hatch;

    private HatchMechanism() {
        hatch = new WPI_TalonSRX(Constants.HATCH);
    }

    public static HatchMechanism getInstance() {
        if (instance == null) {
            instance = new HatchMechanism();
        }
        return instance;
    }

    @Override
    public void outputTelemetry() {
        // TODO(Raina)
    }

    @Override
    public void stop() {
        hatch.stopMotor();
    }
}
