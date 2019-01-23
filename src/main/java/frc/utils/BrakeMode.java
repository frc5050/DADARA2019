package frc.utils;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;

public enum BrakeMode {
    BRAKE(NeutralMode.Brake, CANSparkMax.IdleMode.kBrake),
    COAST(NeutralMode.Coast, CANSparkMax.IdleMode.kCoast);

    private NeutralMode talonBrakeMode;
    private CANSparkMax.IdleMode sparkMaxBrakeMode;

    BrakeMode(NeutralMode talonBrakeMode, CANSparkMax.IdleMode sparkMaxBrakeMode) {
        this.talonBrakeMode = talonBrakeMode;
        this.sparkMaxBrakeMode = sparkMaxBrakeMode;
    }

    public NeutralMode getTalonBrakeMode() {
        return talonBrakeMode;
    }

    public CANSparkMax.IdleMode getSparkMaxBrakeMode(){
        return sparkMaxBrakeMode;
    }
}
