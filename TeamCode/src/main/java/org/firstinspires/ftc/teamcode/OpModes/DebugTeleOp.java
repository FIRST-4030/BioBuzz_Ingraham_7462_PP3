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

@TeleOp(name = "Debug TeleOp", group="Util")
public class DebugTeleOp extends OpMode {

    ControlHub controlHub;

    IMU imu;
    Limelight limelight;
    Chassis chassis;
    Shooter shooter;

    Collector collector;

    @Override
    public void init() {
        controlHub = new ControlHub(hardwareMap);

        imu = hardwareMap.get(IMU.class, "imu");
        limelight = new Limelight(hardwareMap);
        chassis = new Chassis(hardwareMap);
        collector = new Collector(hardwareMap);
        shooter = new Shooter(hardwareMap, limelight, collector);
    }

    public void init_loop() {
        limelight.updateData(); // This line is vital, and should be called first in the loop
        controlHub.processBotIdentificationTelemetry(telemetry);
        Blackboard.initLoopProcess(telemetry, gamepad1);
    }

    public void start() {
        shooter.idleMode();
        //collector.
        limelight.lookForBestAimingPipeline();
    }

    public void loop() {
        limelight.updateData(); // This line is vital, and should be called first in the loop
        handleModeSwitchingControls();

        switch (shooter.getShooterMode()) {
            case IDLE:
            case COLLECTING:
                limelight.lookForBestCollectingPipeline();
                chassis.drive(gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x);
                break;
            case SHOOTING:
                limelight.lookForBestAimingPipeline();
                if (shooter.hasReachedReadyToShoot()) {
                    chassis.lockedOnDrive(gamepad1.left_stick_y, -gamepad1.left_stick_x, limelight);
                } else {
                    chassis.drive(gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x);
                }
                break;
            default:
                chassis.stopMotors();
                break;
        }

        shooter.update();
        handleTelemetry();
    }

    public void handleModeSwitchingControls() {
        if (gamepad1.rightBumperWasPressed()) {
            shooter.idleMode();
            shooter.manualReadyToShoot = false;
        } else if (gamepad1.leftBumperWasPressed()) {
            shooter.idleMode();
            shooter.manualReadyToShoot = false;
        } else if (gamepad1.rightTriggerWasPressed()) {
            shooter.shootingMode();
            shooter.manualReadyToShoot = false;
        } else if (gamepad1.aWasPressed()) {
            shooter.manualReadyToShoot = true;
        }

        if (gamepad1.dpadUpWasPressed()) {
            shooter.tempShootPower += 0.05;
        } else if (gamepad1.dpadDownWasPressed()) {
            shooter.tempShootPower -= 0.05;
        }
    }

    public void handleTelemetry() {
        telemetry.addLine("LB: Idle, RB: Collecting, RT: Aiming");
        telemetry.addLine("Dpad: Up: Faster shooter, DOWN: Slower shooter");
        telemetry.addData("Shooter velocity", shooter.tempShootPower);
        if (!(Blackboard.getAlliance() == Blackboard.Alliance.RED || Blackboard.getAlliance() == Blackboard.Alliance.BLUE)) {
            telemetry.addLine("WARNING: ALLIANCE UNSET! You should re-init!");
        } else {
            telemetry.addData("Alliance", Blackboard.getAlliance());
        }
        telemetry.addLine("-------");
        telemetry.addData("Mode", shooter.getShooterMode());
        telemetry.addData("Ready to shoot?", shooter.readyToShoot());

        telemetry.addLine();

        telemetry.addData("Pipeline", limelight.getCurrentPipelineName());
        telemetry.addData("MS since pipeline switch", limelight.getTimeSinceLastPipelineSwitch().milliseconds());

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
