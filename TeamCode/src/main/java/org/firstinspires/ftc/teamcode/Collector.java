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
    public enum CollectorMode {
        IDLE,
        COLLECT,
        REVERSED
    }

    CollectorMode collectorSpeed;
    public Collector (HardwareMap hardwareMap) {
        collectorMotor = (DcMotorEx) hardwareMap.get(DcMotor.class, "collector");
        collectorMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        collectorMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);  // WITH OUT!
        collectorMotor.setDirection(FORWARD);
    }

    public void setCollectorSpeed(CollectorMode speed) {
        collectorSpeed = speed;
    }

    public CollectorMode getCollectorMode() {
        return collectorSpeed;
    }

    public void setCollector() {
        if (collectorSpeed == CollectorMode.IDLE) {
            collectorMotor.setDirection(FORWARD);
            collectorMotor.setPower(COLLECTOR_LOW_POWER);
        } else if (collectorSpeed == CollectorMode.COLLECT) {
            collectorMotor.setDirection(FORWARD);
            collectorMotor.setPower(COLLECTOR_HIGH_POWER);
        } else if (collectorSpeed == CollectorMode.REVERSED) {
            collectorMotor.setDirection(REVERSE);
            collectorMotor.setPower(COLLECTOR_HIGH_POWER);
        }
    }
}
