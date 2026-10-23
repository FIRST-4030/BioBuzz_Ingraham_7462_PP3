package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Shooter Class.  Implements a feed forward plus feed back control system
 */
public class Shooter {
    private final DcMotorEx nectarShooter;
    private final DcMotorEx pollenShooter;

    // Kvelo is the feed forward term.  Tune first by adjusting until rotational velo target is met
    private double Kvelo = 0.03; // power multiplier for rotations per second

    // FeedBack term is Kp (proportional term)
    // Set Kp to zero when tuning the Kvelo term!!
    private double Kp = 0.7;  // no gain in improvement when increasing beyond this

    private static final double   COUNTS_PER_REV = 28 ;  // REV HD Hex 1:1 Motor Encoder

    private double targetVelocity = 0;  // rotations per second (max is ~40)
    private double veloPower = 0; // used for determining Kp

    /**
     * Shooter Constructor
     * @param hardwareMap FTC harware map
     * @param leftName  The config file name for the motor
     * @param dir Direction Boolean
     */
    public Shooter(HardwareMap hardwareMap, String leftName, String rightName, Boolean dir) {
        nectarShooter = (DcMotorEx) hardwareMap.get(DcMotor.class, leftName);
        pollenShooter = (DcMotorEx) hardwareMap.get((DcMotor.class), rightName);
        //shooter.setDirection(DcMotor.Direction.FORWARD);
        nectarShooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        nectarShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        pollenShooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pollenShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // WITH OUT!
        setMotorDirection(dir);

    }

    /**
     * overridePower method should be called in the main loop()
     */
    public void overridePower() {
        double currentVelocity = pollenShooter.getVelocity(AngleUnit.DEGREES)/COUNTS_PER_REV;
        double veloError = targetVelocity - currentVelocity;
        // CONTROLLER:  feedfoward = Kvelo + feedback = Kpos
        veloPower = veloError * Kp;
        double setPower = targetVelocity * Kvelo + veloPower;
        nectarShooter.setPower(setPower);
        pollenShooter.setPower(setPower);
    }
    private void setMotorDirection(Boolean dir) {
        //True = forward, false = backwards
        if (dir) {
            nectarShooter.setDirection(DcMotor.Direction.REVERSE);
            pollenShooter.setDirection(DcMotor.Direction.FORWARD);
        } else {
            nectarShooter.setDirection(DcMotor.Direction.FORWARD);
            pollenShooter.setDirection(DcMotor.Direction.REVERSE);
        }
    }

    /**
     * Set the Control terms for the flywheel controller
     * @param Kp Proportional feedback term
     * @param Kvelo Feed Forward Velocity term
     */
    public void setControllerValues(double Kp, double Kvelo) {
        this.Kp = Kp;
        this.Kvelo = Kvelo;
    }

    public void setTargetVelocity(double velo) { this.targetVelocity = velo;  }
    public double getPower() { return pollenShooter.getPower();   }
    public double getVeloPower() { return veloPower;}
    public double getVelocity() {
        return pollenShooter.getVelocity(AngleUnit.DEGREES)/COUNTS_PER_REV;
    }

    /**
     * atSpeed method lets one know if the motor is running at the target speed
     * @return Boolean True is at speed
     */
    public boolean atSpeed() {
        return 0.98 * targetVelocity < this.getVelocity() && this.getVelocity() < 1.02 * targetVelocity;
    }

    /**
     * getShooterVelo method computes velocity from range using function based on shooting experiments
     * @param limelight Limelight pointer
     * @return Velocity that shooter should run at for found distance
     */
    public double getShooterVelo(Limelight limelight) {

        double range = limelight.getRange();
        double poly = 19.0 + 0.125 * range;
        if (range < 80.0) poly = 29.0;

        return poly;
    }

    public void setPower(double power) {
        nectarShooter.setPower(power);
        pollenShooter.setPower(power);
    }
    public double getTargetVelocity() {return targetVelocity;}
}
