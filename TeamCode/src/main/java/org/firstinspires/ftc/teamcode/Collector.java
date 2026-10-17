package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Collector {

    DcMotor collectorMotor;
    public enum COLLECTOR_SPEED {
        IDLE,
        COLLECT
    }
    public Collector (HardwareMap hardwareMap) {
        collectorMotor = (DcMotorEx) hardwareMap.get(DcMotor.class, "collector");
        collectorMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        collectorMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);  // WITH OUT!
        collectorMotor.setDirection(REVERSE);
    }

    public void collectingMode(COLLECTOR_SPEED mode) {
        if (mode == COLLECTOR_SPEED.IDLE) {
            collectorMotor.setDirection(FORWARD);
        } else {
            collectorMotor.setDirection(REVERSE);
        }
    }

    public void setPower(boolean direction, double power) {
        if (direction) {
            collectorMotor.setDirection(FORWARD);
        } else {
            collectorMotor.setDirection(REVERSE);
        }
        collectorMotor.setPower(power);
    }
}
