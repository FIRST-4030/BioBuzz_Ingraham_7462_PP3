

package org.firstinspires.ftc.teamcode.OpModes;

import static com.pedropathing.ivy.Scheduler.schedule;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.pedro.Constants;


@Autonomous(name="FionaPractice", group="Robot")

public class FionaPractice extends LinearOpMode {
    private Follower follower;
    /* Declare OpMode members. */
    private Servo spin = null;


    /**
     * The variable to store a reference to our color sensor hardware object
     */
    public void runOpMode() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);

        // Initialize the drive system variables.
        spin = hardwareMap.get(Servo.class, "spinServo");
        waitForStart();
        schedule(autoRoutine());
        while (opModeIsActive())
        {
            follower.update();
            Scheduler.execute();
        }



    }

    private Command autoRoutine() {
        return null;
    }
}



















