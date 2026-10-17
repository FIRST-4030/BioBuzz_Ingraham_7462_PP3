package org.firstinspires.ftc.teamcode.OpModes;

import com.pedropathing.math.Vector2D;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.UtilOpModes.Poses;
import org.firstinspires.ftc.teamcode.pedro.Constants;

import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Scheduler;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static com.pedropathing.api.Paths.*;



@Autonomous
public class PP3AutoTest extends OpMode {
    private Follower follower;
    //bank of poses
    Poses poses = new Poses();
    @Override
    public void init() {
        //Reset movements
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        //initializes follower
        follower = Constants.create(hardwareMap);

        //sets pose to "start"
        follower.setPose(poses.autoStartPos);
        follower.update();
    }
    public Path curveWhilePointing(){
        return curve(poses.testStart,poses.testControlPoint, poses.testMid).facingPoint(Vector2D.cartesian(70, 70));
    }

    @Override
    public void start() {
        schedule(follow(follower, curveWhilePointing()));
    }

    @Override
    public void loop() {
        Scheduler.execute();
        follower.update();
    }
}
