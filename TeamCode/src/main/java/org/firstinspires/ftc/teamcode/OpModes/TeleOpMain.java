package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.generalUtilities.Blackboard;
import org.firstinspires.ftc.teamcode.Collector;
import org.firstinspires.ftc.teamcode.BiobuzzShooter;
import org.firstinspires.ftc.teamcode.Chassis;
import org.firstinspires.ftc.teamcode.ControlHub;
import org.firstinspires.ftc.teamcode.Limelight;

@TeleOp(name = "TeleOpMain")
public class TeleOpMain extends OpMode {

    ControlHub controlHub;

    IMU imu;
    Limelight limelight;
    Chassis chassis;
    BiobuzzShooter shooter;
    Collector collector;
    ElapsedTime shotTimer;

    private double shooterSpeedRPM = -1500;

    public enum RunMode {
        IDLE,
        COLLECTING,
        SHOOTING,
    }

    public RunMode runMode = RunMode.IDLE;

    @Override
    public void init() {
        controlHub = new ControlHub(hardwareMap);

        imu = hardwareMap.get(IMU.class, "imu");
        limelight = new Limelight(hardwareMap);
        chassis = new Chassis(hardwareMap);
        chassis.setMaxSpeed(1.0);
        collector = new Collector(hardwareMap);
        shooter = new BiobuzzShooter(hardwareMap, limelight);
        shotTimer = new ElapsedTime();
    }

    public void init_loop() {
        limelight.updateData(); // This line is vital, and should be called first in the loop
        controlHub.processBotIdentificationTelemetry(telemetry);
        Blackboard.initLoopProcess(telemetry, gamepad1);
    }

    public void start() {
        collector.setCollectorSpeed(Collector.CollectorMode.IDLE);
        collector.setCollector();
        shooter.setShooterMode(BiobuzzShooter.ShooterMode.IDLE);
        shooter.setShooterDirection();
        shooter.resetTargetVelocity();
        shooter.overridePower();

        limelight.lookForBestAimingPipeline();
    }

    public void loop() {
        limelight.updateData(); // This line is vital, and should be called first in the loop
        handleModeSwitchingControls();

        switch (runMode) {
            case IDLE:
                collector.setCollectorSpeed(Collector.CollectorMode.IDLE);
                collector.setCollector();

                shooter.setShooterMode(BiobuzzShooter.ShooterMode.IDLE);
                shooter.setShooterDirection();
                shooter.resetTargetVelocity();

                chassis.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
                break;
            case COLLECTING:
                collector.setCollectorSpeed(Collector.CollectorMode.COLLECT);
                collector.setCollector();

                shooter.setShooterMode(BiobuzzShooter.ShooterMode.IDLE);
                shooter.setShooterDirection();
                shooter.resetTargetVelocity();

                chassis.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
                break;
            case SHOOTING:
                limelight.lookForBestAimingPipeline();
                if (shooter.manualReadyToShoot) {
                    collector.setCollectorSpeed(Collector.CollectorMode.COLLECT);
                    collector.setCollector();

                    shooter.setShooterMode(BiobuzzShooter.ShooterMode.SHOOTING);
                    shooter.setShooterDirection();
                    shooter.setTargetVelocity(shooterSpeedRPM);

                    chassis.lockedOnDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, limelight);
                } else {
                    collector.setCollectorSpeed(Collector.CollectorMode.IDLE);
                    collector.setCollector();

                    shooter.setShooterMode(BiobuzzShooter.ShooterMode.SHOOTING);
                    shooter.setShooterDirection();
                    shooter.setTargetVelocity(shooterSpeedRPM);

                    chassis.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
                }
                break;
            default:
                chassis.stopMotors();
                break;
        }

        shooter.overridePower();
        handleTelemetry();
    }

    public void handleModeSwitchingControls() {

        if (gamepad1.right_trigger_pressed) {
            runMode = RunMode.SHOOTING;
            telemetry.addLine("RunMode: SHOOTING");
        } else if (gamepad1.left_trigger_pressed) {
            runMode = RunMode.COLLECTING;
            telemetry.addLine("RunMode: COLLECTING");
        } else {
            runMode = RunMode.IDLE;
            telemetry.addLine("RunMode: IDLE");
        }

        if (gamepad1.aWasPressed()) {
            shooter.manualReadyToShoot = true;
        } else if (gamepad1.aWasReleased()) {
            shooter.manualReadyToShoot = false;
        }

        if (gamepad1.leftBumperWasPressed()) {
            chassis.setMaxSpeed(0.5);
        } else if (gamepad1.leftBumperWasReleased()) {
            chassis.setMaxSpeed(1.0);
        }

        if (gamepad1.dpadUpWasPressed()) {
            shooterSpeedRPM -= 100;
        } else if (gamepad1.dpadDownWasPressed()) {
            shooterSpeedRPM += 100;
        } else if (gamepad1.dpadLeftWasPressed()) {
            shooterSpeedRPM = shooter.SHOOTER_CLOSE_SPEED;
        } else if (gamepad1.dpadRightWasPressed()) {
            shooterSpeedRPM = shooter.SHOOTER_FAR_SPEED;
        }
    }

    public void handleTelemetry() {
        telemetry.addLine("LT: Collecting, RT: Shooting, A: Shoot");
        telemetry.addLine("Dpad: Up: Faster shooter, DOWN: Slower shooter");

        telemetry.addLine("-------");

        telemetry.addData("Shooter Target Velocity", shooter.getTargetVelocity());
        telemetry.addData("Current Shooter Velo (RPM)", shooter.getVelocity());
        telemetry.addData("VeloError", shooter.getVeloError());
        telemetry.addData("veloPower", shooter.getVeloPower());

        telemetry.addLine("-------");

        telemetry.update();
    }
}