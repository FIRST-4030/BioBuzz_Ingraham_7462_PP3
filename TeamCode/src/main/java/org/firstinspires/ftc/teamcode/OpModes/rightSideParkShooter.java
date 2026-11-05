package org.firstinspires.ftc.teamcode.OpModes;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Collector;
import org.firstinspires.ftc.teamcode.ControlHub;
import org.firstinspires.ftc.teamcode.Limelight;
import org.firstinspires.ftc.teamcode.Poses;

@Autonomous(name = "Right Side Park Shoot")
public class rightSideParkShooter extends OpMode {
    ControlHub controlHub;
    private Follower follower;
    private Collector collector;
    private Limelight limelight;
    private ElapsedTime elapsedTime = new ElapsedTime();
    //bank of poses
    Poses poses = new Poses();
    private Path StartToShoot() {
        return line(poses.startPosRight, poses.shootPosRight).linear(poses.startPosRight, poses.shootPosRight);
    }
    private Path ShootToPrePark(){
        return line(poses.shootPosRight, poses.preParkPosRight).linear(poses.shootPosRight, poses.preParkPosRight);
    }
    private Path Park(){
        return line(poses.preParkPosRight, poses.parkPosRight).linear(poses.preParkPosRight, poses.parkPosRight);
    }

    @Override
    public void init() {
        controlHub = new ControlHub(hardwareMap);
        collector = new Collector(hardwareMap);
        limelight = new Limelight(hardwareMap);

        limelight.setPipeline(1);

        //Reset movements
        Scheduler.reset();
        follower = controlHub.getRobotSpecificPedroConstants();

        //sets pose to "start"
        follower.setPose(poses.startPosRight);
        follower.update();
    }
    @Override
    public void start() {
        schedule(autoRoutine());
        elapsedTime.reset();
    }

    @Override
    public void loop() {
        Scheduler.execute();
        follower.update();
    }

    private Command autoRoutine(){
        return sequential(
                instant(() -> {collector.setCollectorSpeed(Collector.CollectorMode.COLLECT);}),
                follow(follower, StartToShoot()),
                waitUntil(() ->
                        limelight.canSeeHiveCell(Limelight.HiveCell.BLUE_SCORING) ||
                        limelight.canSeeHiveCell(Limelight.HiveCell.RED_AUDIENCE) ||
                        elapsedTime.seconds() > 20
                ),
                instant(()->{
                    if(elapsedTime.seconds() > 20){
                        collector.setCollectorSpeed(Collector.CollectorMode.IDLE);
                        //shoot
                    }
                }),

                follow(follower, ShootToPrePark()),
                follow(follower, Park())
        );
    }
}