package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Shooter {

    DcMotorEx leftMotor;
    DcMotorEx rightMotor;

    // This class is mostly reworked from 8628's DECODE season "Shooter" class, and is intended as
    // a general-purpose flywheel leftMotor controller for both our collector and shooter flywheels.

    // These should be tuned per-flywheel ideally; but just defaults to the values from DECODE
    public double KVelocity = 0.0243; // power multiplier for rotations per second
    // FeedBack term is Kp (proportional term)
    // Set Kp to zero when tuning the KVelocity term!!
    public double Kp = 0.3;

    private static final double COUNTS_PER_REV = 28 ;  // REV HD Hex 1:1 leftMotor Encoder
    private static final double errorMargin = 0.02;
    private static final double COLLECTOR_LOW_POWER = 0.5;
    private static final double COLLECTOR_HIGH_POWER = 1.0;

    private double targetVelocity = 0;
//    public Flywheel flipperFlywheel;
    Limelight limelight;
    Collector collector;

    private boolean reachedReadyToShoot = false;

    public boolean manualReadyToShoot = false; // Last resort
    // TODO: THIS IS TEMPORARY
    public double tempShootPower = 1;

    private static final double MIN_ANGLE_DEG = 56.0;
    private static final double MAX_ANGLE_DEG = 140.0;

    public enum ShooterMode {
        IDLE,
        COLLECTING,
        SHOOTING
    }

    private ShooterMode shooterMode = ShooterMode.COLLECTING;

    public Shooter(HardwareMap hardwareMap, Limelight limelight, Collector collector) {

        leftMotor = (DcMotorEx) hardwareMap.get(DcMotor.class, "leftShooter");
        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);  // WITH OUT!
        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        rightMotor = (DcMotorEx) hardwareMap.get(DcMotor.class, "rightShooter");
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);  // WITH OUT!
        rightMotor.setDirection(DcMotor.Direction.FORWARD);

        this.limelight = limelight;
        this.collector = collector;
    }

    public void update() {
//        if (!isPotentiometerConnected()) {
//            shooterFlywheel.stop();
//            return;
//        }

        switch (shooterMode) {
            case IDLE:
                idleModeUpdate();
                break;
            case COLLECTING:
                collectingModeUpdate();
                break;
            case SHOOTING:
                shootBalls();
                break;
        }

        setShooterPower(targetVelocity);
    }

    private void idleModeUpdate() {
        reachedReadyToShoot = false;
        collector.setPower(true, COLLECTOR_LOW_POWER);
    }

    private void collectingModeUpdate() {
        reachedReadyToShoot = false;
        collector.setPower(true, COLLECTOR_HIGH_POWER);
    }

    public void ejectBalls() {
        reachedReadyToShoot = false;
        collector.setPower(false, COLLECTOR_HIGH_POWER);
    }

    private double calculateHingePowerForAngle(double currentArmAngleDegrees, double goalArmAngleDegrees) {
        double clippedGoalArmAngleDegrees = Range.clip(goalArmAngleDegrees, MIN_ANGLE_DEG, MAX_ANGLE_DEG);

        // TODO: Want to try doing it this way too:
        double error = clippedGoalArmAngleDegrees - currentArmAngleDegrees;
        double kP = 0.08;
        double kG = 0.3; // Tuning parameter for gravity compensation

        // Angle is relative to ground (90° = horizontal)
        double feedForward = kG * Math.cos(Math.toRadians(currentArmAngleDegrees));
        double power = (error * kP) + feedForward;
        return Range.clip(power, -1.0, 1.0);
    }

    public ShooterMode getShooterMode() {
        return shooterMode;
    }

    public void setShooterMode(ShooterMode shooterMode) {
        this.shooterMode = shooterMode;
    }

    public void shootingMode() {
        this.shooterMode = ShooterMode.SHOOTING;
    }

    public void idleMode() {
        this.shooterMode = ShooterMode.IDLE;
    }

    public boolean readyToShoot() {
        return (shooterMode == ShooterMode.SHOOTING &&
                limelight.isOnTarget() &&
                atSpeed()) || manualReadyToShoot;
    }

    public void setShooterPower(double power) {
        leftMotor.setPower(power);
        rightMotor.setPower(power);
    }

    public void stop() {
        targetVelocity = 0;
        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }

    public void shootBalls() {
        if (readyToShoot()) {
            collector.setPower(true, COLLECTOR_HIGH_POWER);
        } else {
            collector.setPower(true, COLLECTOR_LOW_POWER);
        }
        reachedReadyToShoot = true;
    }

    public void resetTargetVelocity() {
        targetVelocity = 0;
    }

    public void setTargetVelocity(double velocity) {
        this.targetVelocity = velocity;
    }

    public double getPower() {
        return leftMotor.getPower();
    }

    public double getVelocity() {
        return leftMotor.getVelocity(AngleUnit.DEGREES) / COUNTS_PER_REV;
    }

    public boolean atSpeed() {
        return (1 - errorMargin) * targetVelocity < this.getVelocity() &&
                this.getVelocity() < (1 + errorMargin) * targetVelocity;
    }

    public boolean hasReachedReadyToShoot() {
        return reachedReadyToShoot;
    }
}
