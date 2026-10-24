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
        return line(poses.audienceStartPos, poses.audienceShootPos).linear(poses.audienceStartPos, poses.audienceShootPos);
    }
    private Path AudienceShootToCollect() {
        return curve(poses.audienceShootPos, poses.audienceCollectContPose, poses.audienceCollectPos).linear(poses.audienceShootPos, poses.audienceCollectPos);
    }
    private Path CollectToShoot() {
        return line(poses.audienceCollectPos, poses.audienceShootPos).linear(poses.audienceCollectPos, poses.audienceShootPos);
    }
    private Path ShootToPark() {
        return curve(poses.audienceShootPos, poses.audienceParkContPose, poses.audienceParkPos).linear(poses.audienceShootPos, poses.audienceParkPos);
    }

    private Path startToPark(){
        return curve(poses.audienceStartPos, poses.audienceParkContPose, poses.audienceParkPos).linear(poses.audienceStartPos, poses.audienceParkPos);
    }

    @Override
    public void init() {
        controlHub = new ControlHub(hardwareMap);

        //Reset movements
        Scheduler.reset();
        follower = controlHub.getRobotSpecificPedroConstants();

        //sets pose to "start"
        follower.setPose(poses.audienceStartPos);
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