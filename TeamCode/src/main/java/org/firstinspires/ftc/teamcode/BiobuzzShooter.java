package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class BiobuzzShooter {

    DcMotorEx nectarMotor;
    DcMotorEx pollenMotor;
    DcMotorEx backSpinMotor;

    Servo nectarServo;
    Servo pollenServo;

    Limelight limelight;

    private static final double GEAR_RATIO = 0.5;
    private static final double TICKS_REV = 28;
    private static final double errorMargin = 0.02;

    private double Kvelo = 0.00034; // power multiplier for rotations per second
    private double Kp = 0.0045; // FeedBack term is Kp (proportional term)
    private double veloPower = 0;
    private double veloError;
    private double backKvelo = 0.000174; // power multiplier for rotations per second
    private double backKp = 0.00012; // FeedBack term is Kp (proportional term)
    private double backVeloPower = 0;
    private double backVeloError;

    public double targetVelocity = 0;
    public double backTargetVelo = 0;
    //public double nectarBackTargetVelo = 0;
    public double SHOOTER_CLOSE_SPEED = -1400;
    public double SHOOTER_FAR_SPEED = -1800;

    private boolean reachedReadyToShoot = false;
    public boolean manualReadyToShoot = false;

    public BiobuzzShooter(HardwareMap hardwareMap, Limelight limelight) {

        nectarMotor = (DcMotorEx) hardwareMap.get(DcMotor.class, "nectarShooter");
        nectarMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        nectarMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);  // WITH OUT!
        nectarMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        nectarServo = hardwareMap.get(Servo.class, "nectarServo");

        pollenMotor = (DcMotorEx) hardwareMap.get(DcMotor.class, "pollenShooter");
        pollenMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pollenMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);  // WITH OUT!
        pollenMotor.setDirection(DcMotor.Direction.FORWARD);

        pollenServo = hardwareMap.get(Servo.class, "pollenServo");

        backSpinMotor = (DcMotorEx) hardwareMap.get(DcMotor.class, "backSpinMotor");
//        nectarBackSpin = (DcMotorEx) hardwareMap.get(DcMotor.class, "nectarBackSpin");
//        nectarBackSpin.setDirection(DcMotor.Direction.FORWARD);
//        pollenBackSpin = (DcMotorEx) hardwareMap.get(DcMotor.class, "pollenBackSpin");
//        pollenBackSpin.setDirection(DcMotor.Direction.REVERSE);

        this.limelight = limelight;
    }

    public void overridePower() {
        double currentVelocity = getVelocity();
        veloError = targetVelocity - currentVelocity;
        // CONTROLLER:  feedfoward = Kvelo + feedback = Kpos
        veloPower = veloError * Kp;
        double setPower = targetVelocity * Kvelo + veloPower;
        nectarMotor.setPower(setPower);
        pollenMotor.setPower(setPower);

        double pollenBackCurrentVelocity = getBackSpinVelocity();
        backVeloError = backTargetVelo - pollenBackCurrentVelocity;
        backVeloPower = backVeloError * backKp;
        double setBackPower = backTargetVelo * backKvelo + backVeloPower;
        setBackSpinPower(setBackPower);
    }

    public void setBackSpinPower(double power) {
        backSpinMotor.setPower(-power);
    }

    public void setShooterServos(boolean open) {
        if (open) {
            nectarServo.setPosition(0.7);
            pollenServo.setPosition(0.5);
        } else {
            nectarServo.setPosition(0.2);
            pollenServo.setPosition(0.1);
        }
    }

    public void setPower(double power) {
        pollenMotor.setPower(power);
        nectarMotor.setPower(power);
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

    public void setBackSpinTargetVelocity(double velocity) {
        this.backTargetVelo = velocity;
    }

    public double getPower() {
        return nectarMotor.getPower();
    }

    public double getVelocity() {
        return (nectarMotor.getVelocity() / TICKS_REV) * 60 * GEAR_RATIO;
    }

    public double getBackSpinVelocity() {
        return (backSpinMotor.getVelocity() / TICKS_REV) * 60;
    }

    public boolean atSpeed() {
        return (1 - errorMargin) * targetVelocity > this.getVelocity() &&
                this.getVelocity() > (1 + errorMargin) * targetVelocity;
    }

    public int getTicks() {
        return pollenMotor.getCurrentPosition();
    }
}
