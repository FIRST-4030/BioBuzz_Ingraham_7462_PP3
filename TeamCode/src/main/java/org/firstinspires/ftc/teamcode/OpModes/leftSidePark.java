package org.firstinspires.ftc.teamcode.OpModes;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.ControlHub;
import org.firstinspires.ftc.teamcode.Poses;

@Autonomous(name = "Left Side Park")
public class leftSidePark extends OpMode {
    ControlHub controlHub;
    private Follower follower;
    //bank of poses
    Poses poses = new Poses();
    private Path AudienceStartToShoot() {
        return line(poses.startPosLeft, poses.shootPosLeft).linear(poses.startPosLeft, poses.shootPosLeft);
    }
    private Path AudienceShootToCollect() {
        return curve(poses.shootPosLeft, poses.collectControlPosLeft, poses.shootPosLeft).linear(poses.shootPosLeft, poses.collectPosLeft);
    }
    private Path CollectToShoot() {
        return line(poses.collectPosLeft, poses.shootPosLeft).linear(poses.collectPosLeft, poses.shootPosLeft);
    }
    private Path ShootToPark() {
        return curve(poses.shootPosLeft, poses.parkControlPosLeft, poses.parkPosLeft).linear(poses.shootPosLeft, poses.parkPosLeft);
    }

    private Path startToPark(){
        return curve(poses.startPosLeft, poses.parkControlPosLeft, poses.parkPosLeft).linear(poses.startPosLeft, poses.parkPosLeft);
    }

    @Override
    public void init() {
        controlHub = new ControlHub(hardwareMap);

        //Reset movements
        Scheduler.reset();
        follower = controlHub.getRobotSpecificPedroConstants();

        //sets pose to "start"
        follower.setPose(poses.startPosLeft);
        follower.update();
    }
    @Override
    public void start() {
        schedule(simplifiedRout());
    }

    @Override
    public void loop() {
        Scheduler.execute();
        follower.update();
    }

    private Command autoRoutine(){
        return sequential(
                follow(follower, AudienceStartToShoot()),
                follow(follower, AudienceShootToCollect()),
                follow(follower, CollectToShoot()),
                follow(follower, ShootToPark())
        );
    }
    private Command simplifiedRout(){
        return sequential(
            follow(follower, startToPark())
        );
    }
}