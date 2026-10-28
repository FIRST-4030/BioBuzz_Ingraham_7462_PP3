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

import org.firstinspires.ftc.teamcode.Collector;
import org.firstinspires.ftc.teamcode.ControlHub;
import org.firstinspires.ftc.teamcode.Limelight;
import org.firstinspires.ftc.teamcode.Poses;
import org.firstinspires.ftc.teamcode.Collector.CollectorMode;

@Disabled
@Autonomous(name = "Left Side Park (Blue Team)")
public class leftParkBlue extends OpMode {
    ControlHub controlHub;
    private Collector collector;
    private Limelight limelight;
    private Follower follower;
    //bank of poses
    Poses poses = new Poses();
    private Path startToShoot() {
        return line(poses.startPosLeft, poses.shootPosLeft).linear(poses.startPosLeft, poses.shootPosLeft);
    }
    private Path shootToCollect() {
        return curve(poses.shootPosLeft, poses.collectControlPosLeft, poses.collectPosLeft).linear(poses.shootPosLeft, poses.collectPosLeft);
    }
    private Path collectToShoot() {
        return line(poses.collectPosLeft, poses.shootPosLeft).linear(poses.collectPosLeft, poses.shootPosLeft);
    }
    private Path shootToPark() {
        return curve(poses.shootPosLeft, poses.parkControlPosLeft, poses.parkPosLeft).linear(poses.shootPosLeft, poses.parkPosLeft);
    }

    private Path startToPark(){
        return curve(poses.startPosLeft, poses.collectPosLeft, poses.parkPosLeft).linear(poses.startPosLeft, poses.parkPosLeft);
    }
    private Path collectToPark(){
        return curve(poses.collectPosLeft, poses.parkPosLeft).linear(poses.collectPosLeft, poses.parkPosLeft);
    }

    @Override
    public void init() {
        controlHub = new ControlHub(hardwareMap);
        collector = new Collector(hardwareMap);
        limelight = new Limelight(hardwareMap);
        //Reset movements
        Scheduler.reset();
        follower = controlHub.getRobotSpecificPedroConstants();

        //sets pose to "start"
        follower.setPose(poses.startPosLeft);
        follower.update();
    }
    @Override
    public void start() {
        collector.setCollectorSpeed(CollectorMode.COLLECT);
        schedule(follow(follower, startToShoot()));
        if (true) {
            collector.setCollectorSpeed(CollectorMode.IDLE);
        }
        else if (true){ //if theres not time

        }
        if(true) { //there is time collect
            collector.setCollectorSpeed(CollectorMode.COLLECT);
            schedule(follow(follower, shootToCollect()));
        }
    }

    @Override
    public void loop() {
        Scheduler.execute();
        follower.update();
    }

    private Command autoRoutine(){
        return sequential(
                follow(follower, startToShoot()),
                follow(follower, shootToCollect()),
                follow(follower, collectToShoot()),
                follow(follower, shootToPark())
        );
    }
    private Command simplifiedRout(){
        return sequential(
            follow(follower, startToPark())
        );
    }
}