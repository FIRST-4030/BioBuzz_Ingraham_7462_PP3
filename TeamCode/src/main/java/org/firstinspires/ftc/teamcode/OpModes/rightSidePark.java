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

@Autonomous(name = "Right Side Park")
public class rightSidePark extends OpMode {
    ControlHub controlHub;
    private Follower follower;
    //bank of poses
    Poses poses = new Poses();
    private Path StartToShoot() {
        return line(poses.rightStartPos, poses.shootIG).linear(poses.rightStartPos, poses.shootIG);
    }
    private Path ShootToPrePark(){
        return line(poses.shootIG, poses.preParkPos).linear(poses.shootIG, poses.preParkPos);
    }
    private Path Park(){
        return line(poses.preParkPos, poses.RSParkPos).linear(poses.preParkPos, poses.RSParkPos);
    }

    @Override
    public void init() {
        controlHub = new ControlHub(hardwareMap);

        //Reset movements
        Scheduler.reset();
        follower = controlHub.getRobotSpecificPedroConstants();

        //sets pose to "start"
        follower.setPose(poses.rightStartPos);
        follower.update();
    }
    @Override
    public void start() {
        schedule(autoRoutine());
    }

    @Override
    public void loop() {
        Scheduler.execute();
        follower.update();
    }

    private Command autoRoutine(){
        return sequential(
                follow(follower, StartToShoot()),
                follow(follower, ShootToPrePark()),
                follow(follower, Park())
        );
    }
}