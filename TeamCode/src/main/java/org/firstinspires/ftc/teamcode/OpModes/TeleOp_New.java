package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.generalUtilities.Blackboard;
import org.firstinspires.ftc.teamcode.Collector;
import org.firstinspires.ftc.teamcode.BiobuzzShooter;
import org.firstinspires.ftc.teamcode.Chassis;
import org.firstinspires.ftc.teamcode.ControlHub;
import org.firstinspires.ftc.teamcode.Limelight;

@TeleOp(name = "TeleOp_New")
public class TeleOp_New extends OpMode {

    ControlHub controlHub;

    IMU imu;
    Limelight limelight;
    Chassis chassis;
    BiobuzzShooter shooter;
    Collector collector;
    ElapsedTime shotTimer;

    private double shooterSpeedRPM = -1550;

    boolean shooting = false;

    @Override
    public void init() {
        controlHub = new ControlHub(hardwareMap);

        imu = hardwareMap.get(IMU.class, "imu");
        limelight = new Limelight(hardwareMap);
        chassis = new Chassis(hardwareMap);
        chassis.setMaxSpeed(1.0);
        collector = new Collector(hardwareMap);
        shooter = new BiobuzzShooter(hardwareMap, limelight);
        shotTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    }

    public void init_loop() {
        limelight.updateData(); // This line is vital, and should be called first in the loop
        controlHub.processBotIdentificationTelemetry(telemetry);
    }

    public void start() {
        collector.setCollectorSpeed(Collector.CollectorMode.IDLE);
        collector.setCollector();

        shooter.resetTargetVelocity();
        shooter.overridePower();

        limelight.lookForBestAimingPipeline();
    }

    public void loop() {
        limelight.updateData(); // This line is vital, and should be called first in the loop

        handleControls();
        handleModeControls();
        handleTelemetry();

        shooter.overridePower();

        chassis.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
    }

    public void handleControls() {
        //GAMEPAD 1
        if (gamepad1.leftBumperWasPressed()) {
            chassis.setMaxSpeed(0.5);
        } else if (gamepad1.leftBumperWasReleased()) {
            chassis.setMaxSpeed(1.0);
        }


        //GAMEPAD 2 (not implemented yet b/c of testing)
        if (gamepad1.dpadUpWasPressed()) {
            shooterSpeedRPM -= 50;
        } else if (gamepad1.dpadDownWasPressed()) {
            shooterSpeedRPM += 50;
        } else if (gamepad1.dpadLeftWasPressed()) {
            shooterSpeedRPM = shooter.SHOOTER_CLOSE_SPEED;
        } else if (gamepad1.dpadRightWasPressed()) {
            shooterSpeedRPM = shooter.SHOOTER_FAR_SPEED;
        }
    }

    public void handleModeControls() {
        if (shooting) {
            shoot();
        } else if (gamepad1.rightBumperWasPressed()) {
            shooting = true;
            shotTimer.reset();
        } else if (gamepad1.left_trigger_pressed) {
            collector.setCollectorSpeed(Collector.CollectorMode.COLLECT);
            collector.setCollector();

            shooter.setTargetVelocity(0);
            shooter.setShooterServos(false);
            shooter.setBackSpinPower(0);
        } else {
            collector.setCollectorSpeed(Collector.CollectorMode.IDLE);
            collector.setCollector();

            shooter.setTargetVelocity(0);
            shooter.setShooterServos(false);
            shooter.setBackSpinPower(0);
        }
    }

    public void shoot() {
        collector.setCollectorSpeed(Collector.CollectorMode.IDLE);
        collector.setCollector();
        shooter.setTargetVelocity(shooterSpeedRPM);
        shooter.setBackSpinPower(0.8);

        if (shooter.atSpeed() && shotTimer.milliseconds() <= 3000) {
            shooter.setShooterServos(true);
            collector.setCollectorSpeed(Collector.CollectorMode.COLLECT);
            collector.setCollector();
        } else if (shotTimer.milliseconds() > 3000) {
            shooting = false;
        }
    }

    public void handleTelemetry() {
        telemetry.addData("In shooting mode", shooting);
        telemetry.addData("Shooter Velo", shooter.getVelocity());
        telemetry.addData("Shooter Target Velocity", shooter.getTargetVelocity());
        telemetry.addData("Shooter shoot speed", shooterSpeedRPM);

        telemetry.addLine("-------");

        telemetry.update();
    }
}