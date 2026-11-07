package org.firstinspires.ftc.teamcode.OpModes;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.lazy;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.deadline;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.ivy.behaviors.BlockedBehavior;
import com.pedropathing.ivy.behaviors.ConflictBehavior;
import com.pedropathing.ivy.behaviors.EndCondition;
import com.pedropathing.ivy.behaviors.InterruptedBehavior;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.BiobuzzShooter;
import org.firstinspires.ftc.teamcode.Collector;
import org.firstinspires.ftc.teamcode.ControlHub;
import org.firstinspires.ftc.teamcode.Limelight;
import org.firstinspires.ftc.teamcode.Poses;

import java.util.Collections;
import java.util.Set;
import java.util.function.BooleanSupplier;

@Autonomous(name = "Left side park auto")
public class leftParkAuto extends OpMode {
    //Components and stuff
    ControlHub controlHub;
    private Follower follower;
    private Collector collector;
    private Limelight limelight;
    private BiobuzzShooter shooter;

    //bank of poses
    Poses poses = new Poses();

    //Path methods
    private Path startToShoot() {
        return line(poses.startPosLeft, poses.shootPosLeft).linear(poses.startPosLeft, poses.shootPosLeft);
    }
    private Path collectToShoot() {
        return line(poses.collectPosLeft, poses.shootPosLeft).linear(poses.collectPosLeft, poses.shootPosLeft);
    }
    private Path shootToPreCollect(){
        return line(poses.shootPosLeft, poses.preCollectPoseLeft).facingPoint(poses.collectPosLeft);
    }
    private Path preCollectToCollect(){
        return line(poses.preCollectPoseLeft, poses.collectPosLeft).facingPoint(poses.collectPosLeft);
    }
    private Path robotToPark(){
        Pose currentPose = follower.pose();
        return curve(currentPose, poses.of(24, 24), poses.parkPosLeft).linear(currentPose, poses.parkPosLeft);
    }


    /**
     * Set stuff up
     **/
    @Override
    public void init() {
        //initialize components
        controlHub = new ControlHub(hardwareMap);
        collector = new Collector(hardwareMap);
        limelight = new Limelight(hardwareMap);
        shooter = new BiobuzzShooter(hardwareMap, limelight);

        //april tag pipeline
        limelight.setPipeline(1);

        //Reset movements
        Scheduler.reset();
        follower = controlHub.getRobotSpecificPedroConstants();

        //sets pose to "start"
        follower.setPose(poses.startPosLeft);
        follower.update();
    }

    /**
     * Runs the routine
     **/
    @Override
    public void start() {
        schedule(autoRoutine());
    }

    /**
     * Runs every something i honestly don't know
     **/
    @Override
    public void loop() {
        shooter.overridePower();
        collector.setCollector();
        limelight.updateData();
        Scheduler.execute();
        follower.update();
    }

    /**
     * Contains the logic for the routine and where it wants to go
     * Important Lambdas:   instant runs a code bite
     *                      follow schedules a path to follow
     *                      wait until waits until a bool is met
     * @return  returns sequential containing the routine
     **/
    public BooleanSupplier isAtVelocity = () -> shooter.atSpeed();
    private Command autoLogic(){
        return sequential(
                instant(() -> {
                    collector.setCollectorSpeed(Collector.CollectorMode.COLLECT);
                    shooter.setShooterServos(false);
                    shooter.resetTargetVelocity();
                    shooter.setBackSpinPower(0.9);
                }),
                follow(follower, startToShoot()),
                waitUntil(() ->
                        limelight.canSeeHiveCell(Limelight.HiveCell.RED_AUDIENCE) ||
                        limelight.canSeeHiveCell(Limelight.HiveCell.BLUE_SCORING)
                ),
                instant(() -> shooter.setTargetVelocity(-1460)),
                lazy(() -> waitUntil(isAtVelocity)),
                waitMs(2000),
                instant(()->shooter.setShooterServos(true)),
                waitMs(3000),
                follow(follower, shootToPreCollect()),
                instant(()->shooter.setShooterServos(false)),
                follow(follower, preCollectToCollect()),
                follow(follower, collectToShoot()),
                instant(() -> {
                    collector.setCollectorSpeed(Collector.CollectorMode.COLLECT);
                    shooter.setShooterServos(false);
                    shooter.resetTargetVelocity();
                    shooter.setBackSpinPower(0.8);
                }),
                follow(follower, startToShoot()),
                waitUntil(() ->
                        limelight.canSeeHiveCell(Limelight.HiveCell.RED_AUDIENCE) ||
                                limelight.canSeeHiveCell(Limelight.HiveCell.BLUE_SCORING)
                ),
                instant(() -> {
                    collector.setCollector();
                    shooter.setTargetVelocity(-1500);
                    waitMs(2000);
                }),
                lazy(() -> waitUntil(isAtVelocity)),
                instant(()->shooter.setShooterServos(true)),
                waitMs(5000),
                instant(()->shooter.setShooterServos(false))
        );
    }



    /**
     * this method contains the logic for parking automatically
     * Important Lambdas:   deadline runs two or more processes in parallel and stops all processes once one finishes
     *                      lazy doesn't finalize when the code runs instead it finalizes it values when it's ran
     *
     * @return returns a sequential containing logic for the routine
     **/
    private Command autoRoutine(){
        return sequential(
                deadline(
                        waitMs(20000),
                        autoLogic()
                ),
                instant(()-> collector.setCollectorSpeed(Collector.CollectorMode.IDLE)),
                lazy(() -> follow(follower, robotToPark()))
        );
    }
}