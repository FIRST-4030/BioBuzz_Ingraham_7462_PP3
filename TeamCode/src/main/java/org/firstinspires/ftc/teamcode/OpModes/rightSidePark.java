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
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.ControlHub;
import org.firstinspires.ftc.teamcode.Poses;

@Disabled
@Autonomous(name = "Right Side Park (only use for blue side)")
public class rightSidePark extends OpMode {
    //Components
    ControlHub controlHub;
    private Follower follower;

    //bank of poses
    Poses poses = new Poses();

    //path methods
    private Path StartToShoot() {
        return line(poses.startPosRight, poses.shootPosRight).linear(poses.startPosRight, poses.shootPosRight);
    }
    private Path ShootToPrePark(){
        return line(poses.shootPosRight, poses.preParkPosRight).linear(poses.shootPosRight, poses.preParkPosRight);
    }
    private Path Park(){
        return line(poses.preParkPosRight, poses.parkPosRight).linear(poses.preParkPosRight, poses.parkPosRight);
    }

    /**
     * initialize values and stuff
     */
    @Override
    public void init() {
        controlHub = new ControlHub(hardwareMap);

        //Reset movements
        Scheduler.reset();
        follower = controlHub.getRobotSpecificPedroConstants();

        //sets pose to "start"
        follower.setPose(poses.startPosRight);
        follower.update();
    }

    /**
     * tell the auto to run the desired routine
     */
    @Override
    public void start() {
        schedule(autoRoutine());
    }

    /**
     * keep where the robot in the routine current
     */
    @Override
    public void loop() {
        Scheduler.execute();
        follower.update();
    }

    /**
     * what the robot should do
     *
     * @return sequential containing what the robot should do
     */
    private Command autoRoutine(){
        return sequential(
                follow(follower, StartToShoot()),
                follow(follower, ShootToPrePark()),
                follow(follower, Park())
        );
    }
}