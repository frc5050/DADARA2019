package frc.utils;

public class Constants {
    public static final double EPSILON_SMALL_DOUBLE = 1E-11;

    // Motor controller ports
    public static final int LEFT_DRIVE_1 = 15; // SRX
    public static final int LEFT_DRIVE_2 = 0; // SPX
    public static final int RIGHT_DRIVE_1 = 2; // SRX
    public static final int RIGHT_DRIVE_2 = 3; // SPX
    public static final int HATCH = 12; // SRX
    public static final int INTAKE_TILT = 13; // SRX
    public static final int INTAKE = 1; // SRX
    public static final int FRONT_JACK_LIFT = 11; // SRX
    public static final int RIGHT_REAR_JACK_LIFT = 7; // SRX
    public static final int LEFT_REAR_JACK_LIFT = 4; // SRX
    public static final int RIGHT_REAR_JACK_WHEEL = 6; // SRX
    public static final int LEFT_REAR_JACK_WHEEL = 5; // SRX
    public static final int ELEVATOR_NEO = 14; // MAX
    public static final int CARGO_CENTER = 10; // SRX
    public static final int CARGO_LEFT = 9; // SRX
    public static final int CARGO_RIGHT = 8; // SRX
    public static final DriverHidOption DRIVER_HID_OPTION = DriverHidOption.SINGLE_JOYSTICK;

    // HID ports
    public static final int DRIVER_JOYSTICK_PORT = 0;
    public static final int DRIVER_GAMEPAD_PORT = 0;
    public static final int OPERATOR_GAMEPAD_PORT = 1;
    public static final int DRIVER_JOYSTICK_LEFT_PORT = 0;
    public static final int DRIVER_JOYSTICK_RIGHT_PORT = 2;

    // Sensor ports
    public static final int CARGO_SENSOR = 0; // dio
    public static final int ELEVATOR_BOTTOM_LIMIT = 2; // dio
    public static final int DRIVE_FRONT_IR_SENSOR = 3; // dio
    public static final int DRIVE_REAR_IR_SENSOR = 4; // dio

    // Network Tables Ports
    public static final String DEFAULT_NETWORK_TABLE_KEY = "SmartDashboard";
    public static final boolean USE_CUSTOM_NETWORK_TABLE_KEYS = true;
    public static final ShuffleboardWriter ROBOT_MAIN_SHUFFLEBOARD;
    public static final ShuffleboardWriter LOOPER_SHUFFLEBOARD;
    public static final ShuffleboardWriter DRIVE_SHUFFLEBOARD;
    public static final ShuffleboardWriter ELEVATOR_SHUFFLEBOARD;
    public static final ShuffleboardWriter JACKS_SHUFFLEBOARD;
    public static final ShuffleboardWriter CARGO_SHUFFLEBOARD;
    public static final ShuffleboardWriter TEST_SHUFFLEBOARD;
    public static final ShuffleboardWriter HATCH_SHUFFLEBOARD;
    public static final int CAN_TIMEOUT_MS = 10; // ms
    public static final double DRIVE_VOLTAGE_RAMP_RATE = 0; // time from neutral to full power

    static {
        if (USE_CUSTOM_NETWORK_TABLE_KEYS) {
            ROBOT_MAIN_SHUFFLEBOARD = ShuffleboardWriter.getInstance("RobotMain");
            LOOPER_SHUFFLEBOARD = ShuffleboardWriter.getInstance("Looper");
            DRIVE_SHUFFLEBOARD = ShuffleboardWriter.getInstance("Drive");
            ELEVATOR_SHUFFLEBOARD = ShuffleboardWriter.getInstance("Elevator");
            JACKS_SHUFFLEBOARD = ShuffleboardWriter.getInstance("Jacks");
            CARGO_SHUFFLEBOARD = ShuffleboardWriter.getInstance("Cargo");
            TEST_SHUFFLEBOARD = ShuffleboardWriter.getInstance("Test");
            HATCH_SHUFFLEBOARD = ShuffleboardWriter.getInstance("Hatch");
        } else {
            ROBOT_MAIN_SHUFFLEBOARD = ShuffleboardWriter.getInstance(DEFAULT_NETWORK_TABLE_KEY);
            LOOPER_SHUFFLEBOARD = ShuffleboardWriter.getInstance(DEFAULT_NETWORK_TABLE_KEY);
            DRIVE_SHUFFLEBOARD = ShuffleboardWriter.getInstance(DEFAULT_NETWORK_TABLE_KEY);
            ELEVATOR_SHUFFLEBOARD = ShuffleboardWriter.getInstance(DEFAULT_NETWORK_TABLE_KEY);
            JACKS_SHUFFLEBOARD = ShuffleboardWriter.getInstance(DEFAULT_NETWORK_TABLE_KEY);
            CARGO_SHUFFLEBOARD = ShuffleboardWriter.getInstance(DEFAULT_NETWORK_TABLE_KEY);
            TEST_SHUFFLEBOARD = ShuffleboardWriter.getInstance(DEFAULT_NETWORK_TABLE_KEY);
            HATCH_SHUFFLEBOARD = ShuffleboardWriter.getInstance(DEFAULT_NETWORK_TABLE_KEY);
        }
    }


    private Constants() {

    }

    public enum DriverHidOption {
        SINGLE_JOYSTICK,
        DUAL_JOYSTICKS,
        GAMEPAD
    }
}