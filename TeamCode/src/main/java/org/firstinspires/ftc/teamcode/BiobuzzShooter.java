package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class BiobuzzShooter {

    DcMotorEx nectarMotor;
    DcMotorEx pollenMotor;

    private double Kvelo = 0.0004; // power multiplier for rotations per second
    private double Kp = 0.006; // FeedBack term is Kp (proportional term)
    private double veloPower = 0;

    //private static final double COUNTS_PER_REV = 28 ;  // REV HD Hex 1:1 nectarMotor Encoder
    private static final double GEAR_RATIO = 0.5;
    private static final int TICKS_REV = 28;
    private static final double errorMargin = 0.02;

    public double targetVelocity = 0;
    private double veloError;

    //public double SHOOTER_IDLE_POWER = 0.2;

    public double SHOOTER_IDLE_SPEED = 1000;
    public double SHOOTER_CLOSE_SPEED = 2300;
    public double SHOOTER_FAR_SPEED = 2600;

    //private double shootPower = 0.8;
    Limelight limelight;

    private boolean reachedReadyToShoot = false;

    public boolean manualReadyToShoot = false;

    public enum ShooterMode {
        IDLE,
        SHOOTING
    }

    private ShooterMode shooterMode = ShooterMode.IDLE;

    public BiobuzzShooter(HardwareMap hardwareMap, Limelight limelight) {

        nectarMotor = (DcMotorEx) hardwareMap.get(DcMotor.class, "nectarShooter");
        nectarMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        nectarMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);  // WITH OUT!
        nectarMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        pollenMotor = (DcMotorEx) hardwareMap.get(DcMotor.class, "pollenShooter");
        pollenMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pollenMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);  // WITH OUT!
        pollenMotor.setDirection(DcMotor.Direction.FORWARD);

        this.limelight = limelight;
    }

    public ShooterMode getShooterMode() {
        return shooterMode;
    }

    public void setShooterMode(ShooterMode shooterMode) {
        this.shooterMode = shooterMode;
    }

//    public void setShooterPower(double power) {
//        nectarMotor.setPower(power);
//        pollenMotor.setPower(power);
//    }

    public void setShooterDirection() {
        if (shooterMode == ShooterMode.IDLE) {
            nectarMotor.setDirection(DcMotorSimple.Direction.FORWARD);
            pollenMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        } else if (shooterMode == ShooterMode.SHOOTING) {
            nectarMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            pollenMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        }
    }

//    public void setShootPower(double power) {
//        if (getShootPower() + power > 1) {
//            this.shootPower = 1;
//        } else if (getShootPower() + power < 0) {
//            this.shootPower = 0;
//        } else {
//            this.shootPower = getShootPower() + power;
//        }
//    }

    //RENAME THESE
    //public void setActualShootPower(double newPower) {
    //    shootPower = newPower;
    //}

    //public double getShootPower() {
    //    return shootPower;
    //}

//    public void setShooterLowPower(double power) {
//        if (getShooterLowPower() + power > 0.5) {
//            this.SHOOTER_IDLE_POWER = 0.5;
//        } else if (getShooterLowPower() + power < 0) {
//            this.SHOOTER_IDLE_POWER = 0;
//        } else {
//            this.SHOOTER_IDLE_POWER = getShootPower() + power;
//        }
//    }

//    public double getShooterLowPower() {
//        return SHOOTER_IDLE_POWER;
//    }

    public boolean readyToShoot() {
        return (shooterMode == ShooterMode.SHOOTING &&
                limelight.isOnTarget() &&
                atSpeed()) || manualReadyToShoot;
    }

    public void overridePower() {
        double currentVelocity = getVelocity();
        veloError = targetVelocity - currentVelocity;
        // CONTROLLER:  feedfoward = Kvelo + feedback = Kpos
        veloPower = veloError * Kp;
        double setPower = targetVelocity * Kvelo + veloPower;
        nectarMotor.setPower(setPower);
        pollenMotor.setPower(setPower);
    }

    public void resetTargetVelocity() {
        targetVelocity = 0;
    }

    public double getTargetVelocity() {
        return targetVelocity;
    }

    public void setTargetVelocity(double velocity) {
        this.targetVelocity = velocity;
    }

    public double getPower() {
        return pollenMotor.getPower();
    }

    public double getVelocity() {
        return (pollenMotor.getVelocity() / TICKS_REV) * 60 * GEAR_RATIO;
    }
    public double getVeloError() {
        return veloError;
    }

    public boolean atSpeed() {
        return (1 - errorMargin) * targetVelocity < this.getVelocity() &&
                this.getVelocity() < (1 + errorMargin) * targetVelocity;
    }

    public boolean hasReachedReadyToShoot() {
        return reachedReadyToShoot;
    }

    public int getTicks() {
        return pollenMotor.getCurrentPosition();
    }
}
