package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.generalUtilities.Blackboard;
import org.firstinspires.ftc.teamcode.Collector;
import org.firstinspires.ftc.teamcode.Shooter;
import org.firstinspires.ftc.teamcode.Chassis;
import org.firstinspires.ftc.teamcode.ControlHub;
import org.firstinspires.ftc.teamcode.Limelight;

@TeleOp(name = "TeleOpMain")
public class TeleOpMain extends OpMode {

    ControlHub controlHub;

    IMU imu;
    Limelight limelight;
    Chassis chassis;
    Shooter shooter;

    Collector collector;

    public enum RunMode {
        IDLE,
        COLLECTING,
        SHOOTING
    }

    public RunMode runMode = RunMode.IDLE;

    @Override
    public void init() {
        controlHub = new ControlHub(hardwareMap);

        imu = hardwareMap.get(IMU.class, "imu");
        limelight = new Limelight(hardwareMap);
        chassis = new Chassis(hardwareMap);
        collector = new Collector(hardwareMap);
        shooter = new Shooter(hardwareMap, limelight);
    }

    public void init_loop() {
        limelight.updateData(); // This line is vital, and should be called first in the loop
        controlHub.processBotIdentificationTelemetry(telemetry);
        Blackboard.initLoopProcess(telemetry, gamepad1);
    }

    public void start() {
        collector.setCollectorSpeed(Collector.COLLECTOR_SPEED.IDLE);
        collector.collectingDirection();
        collector.setCollectorPower();
        shooter.setShooterMode(Shooter.ShooterMode.IDLE);
        shooter.setShooterDirection();
        //shooter.setShooterLowPower(0.2);
        shooter.setShooterPower(shooter.getShooterLowPower());

        limelight.lookForBestAimingPipeline();
    }

    public void loop() {
        limelight.updateData(); // This line is vital, and should be called first in the loop
        handleModeSwitchingControls();

        switch (runMode) {
            case IDLE:
                collector.setCollectorSpeed(Collector.COLLECTOR_SPEED.IDLE);
                collector.collectingDirection();
                collector.setCollectorPower();
                shooter.setShooterMode(Shooter.ShooterMode.IDLE);
                shooter.setShooterDirection();
                shooter.setShooterPower(shooter.getShooterLowPower());
                chassis.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
                break;
            case COLLECTING:
                collector.setCollectorSpeed(Collector.COLLECTOR_SPEED.COLLECT);
                collector.collectingDirection();
                collector.setCollectorPower();
                shooter.setShooterMode(Shooter.ShooterMode.IDLE);
                shooter.setShooterDirection();
                shooter.setShooterPower(shooter.getShooterLowPower());
                chassis.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
                break;
            case SHOOTING:
                limelight.lookForBestAimingPipeline();
                shooter.stop();
                if (shooter.manualReadyToShoot) {
                    //hasReachedReadyToShoot()
                    collector.setCollectorSpeed(Collector.COLLECTOR_SPEED.COLLECT);
                    collector.collectingDirection();
                    collector.setCollectorPower();
                    shooter.setShooterMode(Shooter.ShooterMode.SHOOTING);
                    shooter.setShooterDirection();
                    shooter.setShooterPower(shooter.getShootPower());
                    chassis.lockedOnDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, limelight);
                } else {
                    collector.setCollectorSpeed(Collector.COLLECTOR_SPEED.IDLE);
                    collector.collectingDirection();
                    collector.setCollectorPower();
                    shooter.setShooterMode(Shooter.ShooterMode.SHOOTING);
                    shooter.setShooterDirection();
                    shooter.setShooterPower(shooter.getShootPower());
                    chassis.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
                }
                break;
            default:
                chassis.stopMotors();
                break;
        }

        telemetry.addData("Shooter Power", shooter.getShootPower());
        handleTelemetry();
    }

    public void handleModeSwitchingControls() {

        if (gamepad1.right_trigger_pressed) {
            runMode = RunMode.SHOOTING;
            telemetry.addLine("RunMode: SHOOTING");
        }
//        else if (gamepad1.left_trigger < 0.5 && gamepad1.left_trigger >= 0.1) {
//            shooter.stop();
//        }


        else if (gamepad1.left_trigger_pressed) {
            runMode = RunMode.COLLECTING;
            telemetry.addLine("RunMode: COLLECTING");
        } else {
            runMode = RunMode.IDLE;
            telemetry.addLine("RunMode: IDLE");
        }

//        if (gamepad1.rightBumperWasPressed()) {
//            shooter.setShooterMode(Shooter.ShooterMode.IDLE);
//
//            shooter.manualReadyToShoot = false;
//        } else if (gamepad1.rightTriggerWasPressed()) {
//            shooter.setShooterMode(Shooter.ShooterMode.SHOOTING);
//            shooter.manualReadyToShoot = false;
//        } else
        if (gamepad1.aWasPressed()) {
            shooter.manualReadyToShoot = true;
        } else if (gamepad1.aWasReleased()) {
            shooter.manualReadyToShoot = false;
        }

        if (gamepad1.dpadUpWasPressed()) {
            shooter.setShootPower(0.1);
        } else if (gamepad1.dpadDownWasPressed()) {
            shooter.setShootPower(-0.1);
        }

//        if (gamepad1.bWasPressed()) {
//            shooter.setShooterLowPower(0.3);
//        } else if (gamepad1.bWasReleased()) {
//            shooter.setShooterLowPower(-0.3);
//        }
    }

    public void handleTelemetry() {
        //telemetry.addLine("LB: Idle, RB: Collecting, RT: Aiming");
        //telemetry.addLine("Dpad: Up: Faster shooter, DOWN: Slower shooter");
        //telemetry.addData("Shooter velocity", shooter.getShootPower());
        if (!(Blackboard.getAlliance() == Blackboard.Alliance.RED || Blackboard.getAlliance() == Blackboard.Alliance.BLUE)) {
            telemetry.addLine("WARNING: ALLIANCE UNSET! You should re-init!");
        } else {
            telemetry.addData("Alliance", Blackboard.getAlliance());
        }
        telemetry.addLine("-------");
        telemetry.addData("Mode", shooter.getShooterMode());
        telemetry.addData("Ready to shoot?", shooter.readyToShoot());

        telemetry.addLine();

        //telemetry.addData("Pipeline", limelight.getCurrentPipelineName());
        //telemetry.addData("MS since pipeline switch", limelight.getTimeSinceLastPipelineSwitch().milliseconds());

        telemetry.addLine();

        if (limelight.resultHasGoodTargets()) {
            telemetry.addData("On target", limelight.isOnTarget());
            telemetry.addData("Tx", limelight.getXError());
            telemetry.addData("Ty", limelight.getYError());
            telemetry.addData("Ta", limelight.getTa());
        } else {
            telemetry.addLine("No good targets visible");
        }

        telemetry.update();
    }
}