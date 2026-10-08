package org.firstinspires.ftc.teamcode.UtilOpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.pedropathing.follower.Follower;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.Servo;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static com.pedropathing.api.Paths.*;

@Autonomous(name="ExampleAuto")
public class ExampleAuto extends OpMode {
    private Follower follower;
    private final PoseFactory poseFactory = PoseFactory.degrees();

    public static Servo flag;

    // Poses
    private final Pose q = poseFactory.of(60, 8, 90);
    private final Pose w = poseFactory.of(60, 32 ,90);
    private final Pose e = poseFactory.of(20, 32, 180);
    private final Pose r = poseFactory.of(0, 36, 270);

    @Override
    public void init() {
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(q);
        follower.update();

        flag = hardwareMap.get(Servo.class, "flag");
        flag.scaleRange(0,1);
        flag.setPosition(0);

        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.update();
    }

    @Override
    public void start() {
        schedule(autoRoutine());
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
        // add your other methods needed in the loop here

        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.update();
    }

    // Path methods
    public Path one() {
        return line(q, w).linear(q,w);
//        return curve(q, midPoint, w).linear(q,w);
    }

    public Path two(){
        return line(w, e).linear(w,e);
    }

    public Path three(){
        return line(e, r).linear(e,r);
    }

    public Path four(){
        return line(r, q).linear(r,q);
    }

    public Command autoRoutine() {
        return sequential(
            follow(follower, one()),
            turnServo(0.6),
            turnServo(0),
            turnServo(.4),
            turnServo(0),
            waitMs(2000),
                follow(follower, two())

//            follow(follower, three()),
//            follow(follower, four())

        );
    }

    public static Command turnServo(double targetPos) {
        return sequential(instant(() -> flag.setPosition(targetPos)), waitMs(200));
    }
}

