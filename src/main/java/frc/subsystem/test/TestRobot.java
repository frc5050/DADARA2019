package frc.subsystem.test;

// TODO remove this as soon as test functionality is confirmed in the main Robot class
@Deprecated
public class TestRobot {
}
/*extends TimedRobot {


    // TODO should we add manual tests?
    public enum Test {
        DEFAULT_TEST("None"),
        GAMEPAD_TEST("Gamepad Automated Test"),
        DRIVE_TEST("Drive Automated Test"),
        //        DRIVE_MANUAL_CONTROL_TEST("Drive Manual Test"),
        CARGO_TEST("Cargo Automated Test"),
        //        CARGO_MANUAL_TEST("Cargo Manual Test"),
        ELEVATOR_TEST("Elevator Automated Test"),
        //        ELEVATOR_MANUAL_TEST("Elevator Manual Test"),
        JACKS_TEST("Jacks Test"),
        //        JACKS_MANUAL_TEST("Jacks Manual Test"),
        ROBOT_STATE_TEST("Robot State Test");
//        ROBOT_STATE_MANUAL_TEST("Robot State Manual Test");

        private String option;

        Test(String option) {
            this.option = option;
        }

        public String getOption() {
            return option;
        }
    }


    private final SendableChooser<String> chooser = new SendableChooser<>();

    private Looper enabledLooper = new Looper();
    private Looper disabledLooper = new Looper();
    private final SubsystemManager subsystemManager = new SubsystemManager(Arrays.asList(
//            DriveVictorSP.getInstance()
//            Cargo.getInstance(),
            Elevator.getInstance()
//            HatchMechanism.getInstance(),
//            Jacks.getInstance()
    ));

    Elevator elevator = Elevator.getInstance();

    private LinkedHashMap<String, Test> tests = new LinkedHashMap<>();

    @Override
    public void robotInit() {
        SmartDashboard.putString("Robot Init", "Running");
        tests.put(Test.DEFAULT_TEST.getOption(), Test.DEFAULT_TEST);
        for (Test test : Test.values()) {
            if (test != Test.DEFAULT_TEST) {
                tests.put(test.getOption(), test);
            }
        }
        chooser.setDefaultOption(Test.DEFAULT_TEST.getOption(), Test.DEFAULT_TEST.getOption());
        for (String s : tests.keySet()) {
            chooser.addOption(s, s);
        }
        SmartDashboard.putData("Test choices", chooser);

        subsystemManager.registerEnabledLoops(enabledLooper);
        subsystemManager.registerDisabledLoop(disabledLooper);
    }

    @Override
    public void robotPeriodic() {
        outputTelemetry();
    }

    @Override
    public void disabledInit() {
        enabledLooper.stop();
        disabledLooper.start();
    }

    @Override
    public void autonomousInit() {
        disabledLooper.stop();
        enabledLooper.start();
    }

    @Override
    public void autonomousPeriodic() {

    }

    @Override
    public void teleopInit() {
        disabledLooper.stop();
        enabledLooper.start();
    }

    @Override
    public void teleopPeriodic() {
        subsystemManager.outputTelemetry();
    }

    // TODO add more tests
    // TODO automate test validation
    @Override
    public void testInit() {
        Test testSelected = tests.get(chooser.getSelected());
        disabledLooper.stop();
        enabledLooper.start();
        switch (testSelected) {
            case DEFAULT_TEST:
                subsystemTest = null;
                break;
            case GAMEPAD_TEST:
                subsystemTest = new GamepadTest();
                break;
            case DRIVE_TEST:
                subsystemTest = new DriveTest();
                break;
            case CARGO_TEST:
                subsystemTest = new CargoTest();
                subsystemTest = null;
                break;
            case ELEVATOR_TEST:
                subsystemTest = null;
                break;
            case JACKS_TEST:
                subsystemTest = null;
                break;
            case ROBOT_STATE_TEST:
                subsystemTest = null;
                break;
        }
    }

    private SubsystemTest subsystemTest;

    @Override
    public void testPeriodic() {
        subsystemTest.periodic(Timer.getFPGATimestamp());
    }

    public void outputTelemetry() {
        enabledLooper.outputTelemetry();
    }
}
*/