package org.firstinspires.ftc.teamcode.OpModes;

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


@Autonomous
public class PP3ScoringSideAuto extends OpMode {
    ControlHub controlHub;
    private Follower follower;
    //bank of poses
    Poses poses = new Poses();
    private Path StartToEnd(){
        return line(poses.testStart, poses.testMid).linear(poses.testStart, poses.testMid);
    }

    private Command autoRoutine(){
        return sequential(
                follow(follower, StartToEnd())
        );
    }
    @Override
    public void init() {
        controlHub = new ControlHub(hardwareMap);

        //Reset movements
        Scheduler.reset();
        //initializes follower
//        follower = Constants.create(hardwareMap);
        follower = controlHub.getRobotSpecificPedroConstants();

        //sets pose to "start"
        follower.setPose(poses.testStart);
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
}
