package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Collector {

    DcMotor collectorMotor;

    private static final double COLLECTOR_LOW_POWER = 0;
    private static final double COLLECTOR_HIGH_POWER = 1.0;
    public enum COLLECTOR_SPEED {
        IDLE,
        COLLECT,
        REVERSED
    }

    COLLECTOR_SPEED collectorSpeed;
    public Collector (HardwareMap hardwareMap) {
        collectorMotor = (DcMotorEx) hardwareMap.get(DcMotor.class, "collector");
        collectorMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        collectorMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);  // WITH OUT!
        collectorMotor.setDirection(FORWARD);
    }

    public void setCollectorSpeed(COLLECTOR_SPEED speed) {
        collectorSpeed = speed;
    }

    public void collectingDirection() {
        if (collectorSpeed == COLLECTOR_SPEED.IDLE) {
            collectorMotor.setDirection(FORWARD);
        } else if (collectorSpeed == COLLECTOR_SPEED.COLLECT) {
            collectorMotor.setDirection(FORWARD);
        } else if (collectorSpeed == COLLECTOR_SPEED.REVERSED) {
            collectorMotor.setDirection(REVERSE);
        }
    }

    public void setCollectorPower() {
        if (collectorSpeed == COLLECTOR_SPEED.IDLE) {
            collectorMotor.setPower(COLLECTOR_LOW_POWER);
        } else if (collectorSpeed == COLLECTOR_SPEED.COLLECT) {
            collectorMotor.setPower(COLLECTOR_HIGH_POWER);
        } else if (collectorSpeed == COLLECTOR_SPEED.REVERSED) {
            collectorMotor.setPower(COLLECTOR_HIGH_POWER);
        }
    }
}
