package org.firstinspires.ftc.teamcode.OpModes;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.api.Paths.path;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.lazy;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.deadline;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static com.pedropathing.ivy.pedro.PedroCommands.hold;

import com.pedropathing.api.Paths;
import com.pedropathing.config.Modifier;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.ivy.behaviors.BlockedBehavior;
import com.pedropathing.ivy.behaviors.ConflictBehavior;
import com.pedropathing.ivy.behaviors.EndCondition;
import com.pedropathing.ivy.behaviors.InterruptedBehavior;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector2D;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathSegment;
import com.pedropathing.paths.curves.Curve;
import com.pedropathing.paths.curves.bezier.BezierCurve;
import com.pedropathing.paths.interpolator.Interpolator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Collector;
import org.firstinspires.ftc.teamcode.ControlHub;
import org.firstinspires.ftc.teamcode.Limelight;
import org.firstinspires.ftc.teamcode.Poses;

import java.util.Collections;
import java.util.List;
import java.util.Set;


@Autonomous(name = "Test Thing")
public class leftShooterTest extends OpMode {
    ControlHub controlHub;
    private Follower follower;
//    private Collector collector;
//    private Limelight limelight;
//    private ElapsedTime elapsedTime = new ElapsedTime();
    //bank of poses
    Poses poses = new Poses();
    private Path StartToShoot() {
        return line(poses.startPosLeft, poses.shootPosLeft).linear(poses.startPosLeft, poses.shootPosLeft);
    }
    private Path ShootToCollect() {
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

    private Path c1ToC2(){
        return line(poses.arenaCorner1, poses.arenaCorner2).linear(poses.arenaCorner1, poses.arenaCorner2);
    }
    private Path c2ToC1(){
        return line(poses.arenaCorner2, poses.arenaCorner1).linear(poses.arenaCorner2, poses.arenaCorner1);
    }
    private Path startToC1(){
        return line(poses.startPosLeft, poses.arenaCorner1).linear(poses.startPosLeft, poses.arenaCorner1);
    }

    @Override
    public void init() {
        controlHub = new ControlHub(hardwareMap);
//        collector = new Collector(hardwareMap);
//        limelight = new Limelight(hardwareMap);
//
//        limelight.setPipeline(1);

        //Reset movements
        Scheduler.reset();
        follower = controlHub.getRobotSpecificPedroConstants();

        //sets pose to "start"
        follower.setPose(poses.startPosLeft);
        follower.update();
    }
    @Override
    public void start() {
        schedule(autoPark());
//        elapsedTime.reset();
    }

    @Override
    public void loop() {
        Scheduler.execute();
        follower.update();
    }

    private Command autoRoutine(){
        return sequential(
                follow(follower, startToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1()),
                follow(follower, c1ToC2()),
                follow(follower, c2ToC1())

//                instant(() -> {collector.setCollectorSpeed(Collector.CollectorMode.COLLECT);}),
//                follow(follower, StartToShoot()),
//                waitUntil(() ->
//                        limelight.canSeeHiveCell(Limelight.HiveCell.BLUE_SCORING) ||
//                        limelight.canSeeHiveCell(Limelight.HiveCell.RED_AUDIENCE)
//                ),
//                instant(() ->{
//                        collector.setCollectorSpeed(Collector.CollectorMode.IDLE);
//                        //shoot
//                        waitMs(1000);
//                })
        );
    }
    private Path robotToPark(){
        Pose currentPose = follower.pose();
        return curve(currentPose, poses.of(24, 24), poses.parkPosLeft).linear(currentPose, poses.parkPosLeft);
    }
    private Command autoPark(){
        return sequential(
                deadline(
                        waitMs(2000),
                        autoRoutine()
                ),
                lazy(() -> follow(follower, robotToPark()))
        );
    }
    private Command simplifiedRout(){
        return sequential(
                follow(follower, startToPark())
        );
    }
}